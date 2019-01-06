/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.accessor.SealStatus;
import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.LoginFailedException;
import digital.toke.exception.ReadException;

import static digital.toke.RenewalType.*;

/**
 * Base class for Housekeeping (token lifecycle) classes
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public abstract class HousekeepingBase implements Runnable {

	private static final Logger logger = LogManager.getLogger(HousekeepingBase.class);

	protected TokenManager tokenManager;
	protected HousekeepingConfig config;

	public HousekeepingBase(TokenManager parent) {
		this.tokenManager = parent;
		HousekeepingConfig hc = this.tokenManager.getAuth().config.getHousekeepingConfig();
		if(hc == null ) {
			config = HousekeepingConfig.defaultInstance();
		}else {
			config = hc;
		}
	}

	public HousekeepingBase(HousekeepingConfig config, TokenManager parent) {
		this.config = config;
		this.tokenManager = parent;
	}

	/**
	 * If we are configured with unseal keys and 'unseal' is true, we can do this
	 * operation. If not, nothing happens
	 */
	protected void unseal() {

		logger.debug("entering unseal");
		
		Auth auth = tokenManager.getAuth();
		try {
			Toke response = auth.checkSealStatus();
			SealStatus vaultInstance = new SealStatus(response);
			if (vaultInstance.isSealed()) {
				// check to see if we should attempt unsealing
				if (config.unseal && (config.unsealKeys != null) && (config.unsealKeys.size()>0)) {
					try {
						response = auth.unseal(config.getUnsealKeys(), false, false);
					} catch (ConfigureException e) {
						logger.error(e);
						response = null;
					}
					
					if(response != null) {
						vaultInstance = new SealStatus(response);
						if (vaultInstance.isSealed()) {
							logger.error("expected to unseal, but failed..." + vaultInstance.json().toString());
						} else {
							logger.info("Unsealed successfully..." + vaultInstance.json().toString());
						}
					}else {
						logger.error("Bad response?");
						return;
					}
				}else {
					logger.info("Vault sealed, but conditions not met in config to attempt unseal");
				}
			} else {
				logger.info("Vault instance appears to be unsealed  - good.");
			}
		} catch (ReadException e1) {
			logger.error(e1);
		}catch(NullPointerException z) {
			z.printStackTrace();
			logger.error(z);
		}
	}

	/**
	 * This method needs to return immediately if useCachedTokens is not set
	 */
	protected void loadCachedTokens() {

	}

	/**
	 * Should be idempotent
	 */
	protected void login() {
		// 1.0 - do we have any tokens? If not, try to get one.
		// The initial TokenEvent sent of a valid token will free the latch on the
		// restful client operations when we are ready to go

		final Auth auth = tokenManager.getAuth();
		final Set<Token> tokens = tokenManager.getManagedTokens();

		if (tokens.size() == 0) {

			logger.info("Zero tokens found, trying to login to get one...");

			Token token = null;
			try {
				token = auth.login();
				if (token.fromSuccessfulLoginRequest) {
					
					tokenManager.fireLoginEvent(token);
					tokens.add(token);
				}
				
			} catch (LoginFailedException e) {
				logger.error(e);
				return;
			}
			
			// requires read permission on /auth/token/lookup-self
			Token updated = null;
			try {
				updated = auth.lookupSelf(token);
				logger.debug("updated token with lookup data " + updated.lookupData.toString());
				tokens.add(updated);
				tokenManager.fireLoginEvent(token);
				return; // exit at this point - we are logged in
			} catch (ReadException e) {
				// maybe we didn't have permission
				logger.error(e);
				return;
			}
			
		}
	}

	/**
	 * Tokens with no lookup data can dynamically get it through this call - should
	 * only be called after login. Should be idempotent.
	 */
	public void tokenLookup() {

		final Auth auth = tokenManager.getAuth();
		final Set<Token> tokens = tokenManager.getManagedTokens();

		int initialCount = tokens.size();
		List<Token> updatedTokens = new ArrayList<Token>();
		Iterator<Token> iter = tokens.iterator();
		while (iter.hasNext()) {
			Token t = iter.next();
			if (!t.getLookupData().has("data")) {
				try {
					// requires read permission on /auth/token/lookup-self
					Token updated = auth.lookupSelf(t);
					logger.debug("updated token with lookup data " + updated.lookupData.toString());
					updatedTokens.add(updated);
					tokenManager.fireTokenEvent(new TokenEvent(this, updated, EventEnum.RELOAD_TOKEN));
				} catch (ReadException e) {
					logger.error(e);
				}
			} else {
				// just add so we keep it
				updatedTokens.add(t);
			}
		}

		// 1.1.1 - remove old token instances
		for (Token t : updatedTokens) {
			if (tokens.contains(t))
				tokens.remove(t);
		}
		// - refresh with updated ones
		for (Token t : updatedTokens) {
			tokens.add(t);
		}

		// - sanity test

		if (tokens.size() != initialCount) {
			logger.error("this could happen if equals() in Token class is not working properly");
		} else {
			logger.debug("matching count in tokens after update: " + tokens.size());
		}
	}

	/**
	 * Attempt to renew - if there is an exception, try to replace the token by
	 * logging in
	 * 
	 */
	public List<TokenRenewal> renew() {
		List<TokenRenewal> renewals = new ArrayList<TokenRenewal>();
		final Set<Token> tokens = tokenManager.getManagedTokens();
		if(tokens.size() == 0) return renewals; // bail if nothing to renew
	
		final Auth auth = tokenManager.getAuth();
	
		Iterator<Token> iter = tokens.iterator();
		while (iter.hasNext()) {
			Token oldToken = iter.next();
			if (oldToken.isRenewable()) {

				if (oldToken.isPeriodic()) {
					try {
						Token newToken = auth.renewPeriodic(oldToken);
						renewals.add(new TokenRenewal(PERIODIC, oldToken,newToken));
					} catch (Exception x) {
						logger.info("Renew Periodic has failed, will try to reauthenticate and get a new token.", x);
						try {
							Token newToken = auth.login();
							renewals.add(new TokenRenewal(LOGIN,oldToken,newToken));
						} catch (LoginFailedException e) {
							logger.error(e);
							logger.error("giving up here...");
						}
					}

					continue;
				}

				ZonedDateTime zdt = oldToken.expireTime();
				// instant can be null if this is root, possibly others...?
				if (zdt != null) {
					Instant instant = zdt.toInstant();
					long count = Instant.now().until((Temporal) instant, ChronoUnit.SECONDS);
					logger.info("Token with accessor " + oldToken.accessor() + " will expire in " + count + " seconds.");
					if (config.renew) {
						logger.debug(String.format("Checking renew... min_ttl: %d, count: %d", config.min_ttl, count));
						if (config.min_ttl > count) {
							logger.debug("OK, looks like should renew now");
							// ok, try to do renewal
							try {
								Token newToken = auth.renewSelf(oldToken);
								renewals.add(new TokenRenewal(SELF,oldToken,newToken));
							} catch (Exception e) {
								logger.info(
										"Renew of non-periodic token has failed, will try to reauthenticate and get a new token.",
										e);
								try {
									Token newToken = auth.login();
									renewals.add(new TokenRenewal(LOGIN,oldToken,newToken));
								} catch (LoginFailedException z) {
									logger.error(z);
									logger.error("giving up here...");
								}
							}

							continue;
						}

					} else {
						logger.debug("Not yet in range to renew.");
					}
				}
			} else {
				logger.debug("token is not renewable, so doing nothing...");
			}
		}

		return renewals;
	}
}
