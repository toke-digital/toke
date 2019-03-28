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
import digital.toke.exception.ConfigureException;
import digital.toke.exception.LoginFailedException;
import digital.toke.exception.ReadException;

import static digital.toke.RenewalType.*;

/**
 * Base class for Housekeeping (token lifecycle) classes
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
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
			config = new HousekeepingConfig.Builder().build(); // defaults
		}else {
			config = hc;
		}
	}

	public HousekeepingBase(HousekeepingConfig config, TokenManager parent) {
		this.config = config;
		this.tokenManager = parent;
	}
	
	/**
	 * If init is successful, it sets up to run unseal immediately ad login with the root token. 
	 */
	protected void initVault() {
		
		// very special case, we are initializing a new vault instance. 
		if(this.config.init) {
			try {
				
				// TODO, find out if it makes sense to try to init at all ....
				
				Auth auth = tokenManager.getAuth();
				TokeDriverConfig driverConfig = tokenManager.getDriverConfig();
				Toke response = auth.initVault();
				logger.debug("Result of init call: "+response.response);
				response.init().writeKeysToFile(this.config.keyFile);
				
				// this will set up initial unseal - this vault has never been unsealed before
				this.config.unsealKeys = response.init().keys();
				this.config.unseal = true;
				
				// this will set up our initial root login
				driverConfig.setToken(response.init().rootToken());
				driverConfig.authType = AuthType.TOKEN;
				
				logger.debug("root token set in config, you should be good to go for unseal");
				
			} catch (ConfigureException e) {
				e.printStackTrace();
				return;
			}
			
			// if attempt successful, unset flag for init, we do not want to re-try this
			this.config.init = false;
		}
	}
	
	/**
	 * If we are configured with unseal keys and 'unseal' is true, and we have unseal keys, we can do this
	 * operation. If not, nothing happens
	 */
	protected void unseal() {

		logger.debug("entering unseal");
		
		Auth auth = tokenManager.getAuth();
		try {
			Toke response = auth.checkSealStatus();
			SealStatus vaultInstance = new SealStatus(response);
			if (vaultInstance.isSealed()) {
				logger.info("Notice: vault is sealed and we will attempt to unseal if the conditions for that have been met");
				// check to see if we should attempt unsealing
				if (config.unseal && (config.unsealKeys != null) && (config.unsealKeys.size()>0)) {
					try {
						logger.info("conditions met to attempt unseal: 1) requested, 2) unsealKeys is set; 3) unseal keys size is greater than 0");
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
					logger.error("Problem: vault sealed, but conditions not met in config to attempt unseal");
				}
			} else {
				logger.info("Vault instance appears to be unsealed  - good. Exiting this method.");
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
		logger.info("No cached tokens to load");
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

			logger.info("Zero managed tokens found, trying to login to get one...");

			Token token = null;
			try {
				token = auth.login();
				
				// needed for setting header on next call
				tokenManager.fireLoginEvent(token);
			} catch (LoginFailedException e) {
				logger.error(e);
				return;
			}
			
			// requires read permission on /auth/token/lookup-self
			try {
				token = auth.lookupSelf(token);
				logger.debug("updated token with lookup data " + token.lookupData.toString());
				tokens.add(token);
				tokenManager.fireLoginEvent(token);
				return; // exit at this point - we are logged in and lookup complete
			} catch (ReadException e) {
				// maybe we didn't have permission
				logger.error("Does this user have permission to read auth/token/lookup-self?", e);
				return;
			}
			
		}
	}

	
	/**
	 * Attempt to renew - if there is an exception, try to replace the token by logging in
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
			
			if (oldToken.isRenewable()) {
				logger.debug("token is renewable...");
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
								e.printStackTrace();
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
