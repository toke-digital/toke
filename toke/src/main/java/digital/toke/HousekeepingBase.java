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

public abstract class HousekeepingBase implements Runnable {

	private static final Logger logger = LogManager.getLogger(HousekeepingBase.class);

	protected TokenManager tokenManager;
	protected HousekeepingConfig config;

	public HousekeepingBase(TokenManager parent) {
		this.tokenManager = parent;
		config = HousekeepingConfig.defaultInstance();
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

		Auth auth = tokenManager.getAuth();
		try {
			Toke response = auth.checkSealStatus();
			SealStatus vaultInstance = new SealStatus(response);
			if (vaultInstance.isSealed()) {
				// check to see if we should attempt unsealing
				if (config.unseal && config.unsealKeys != null) {
					try {
						Toke resp = auth.unseal(config.getUnsealKeys(), false, false);
						SealStatus status = new SealStatus(resp);
						if (status.isSealed()) {
							logger.error("expected to unseal, but failed..." + status.json().toString());
						} else {
							logger.info("Unsealed successfully..." + status.json().toString());
						}
					} catch (ConfigureException e) {
						logger.error(e);
					}
				}
			} else {
				logger.info("Vault instance appears to be unsealed  - good.");
			}
		} catch (ReadException e1) {
			logger.error(e1);
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
		// 1.0 - do we have any tokens? If not, get one.
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
					// requires read permission on /auth/token/lookup-self
					Token updated = null;
					try {
						updated = auth.lookup(token);
						logger.debug("updated token with lookup data " + updated.lookupData.toString());
						tokens.add(updated);
						tokenManager.fireLoginEvent(token);
						return; // exit at this point - we are logged in
					} catch (ReadException e) {
						// maybe we didn't have permission, keep the good token anyway
						logger.error(e);
						tokens.add(updated);
						tokenManager.fireLoginEvent(token);
						return; // exit at this point - we are logged in
					}
				} else {
					logger.info("Unsuccessful login attempt...");
					logger.debug(token.getJson().toString());
				}
			} catch (LoginFailedException e) {
				logger.error(e);
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
					Token updated = auth.lookup(t);
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

	public void renew() {
		
		final Auth auth = tokenManager.getAuth();
		final Set<Token> tokens = tokenManager.getManagedTokens();
		Iterator<Token> iter = tokens.iterator();
		while(iter.hasNext()) {
			Token t = iter.next();
		if (t.isRenewable()) {
			ZonedDateTime zdt = t.expireTime();
			// instant can be null if this is root, possibly others...?
			if (zdt != null) {
				Instant instant = zdt.toInstant();
				long count = Instant.now().until((Temporal) instant, ChronoUnit.SECONDS);
				logger.info("Token with accessor " + t.accessor() + " will expire in " + count + " seconds.");
				if (config.renew) {
					logger.debug(String.format("Checking renew... min_ttl: %d, count: %d", config.min_ttl, count));
					if (config.min_ttl > count) {
						logger.debug("OK, looks like should renew now");
						// ok, new renew
						
						
						
					    
						
					}else {
						logger.debug("Not yet in range to renew.");
					}
				}
			}
		}else {
			logger.debug("token is not renewable, so doing nothing...");
		}
		}
	}
}
