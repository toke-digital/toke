/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import digital.toke.exception.OutOfTokensException;


/**
 * TokenManager looks after token life-cycle and can do auto-renewals, etc. It
 * allows the Driver to continue operations essentially indefinitely, as in an
 * enterprise application which regularly needs to contact vault.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class TokenManager {

	private static final Logger logger = LogManager.getLogger(TokenManager.class);

	private final Auth auth;
	private final Set<Token> tokens;
	private final List<TokenListener> listeners;

	private ScheduledExecutorService scheduledPool;

	public TokenManager(Auth auth) {
		this.auth = auth;
		tokens = new HashSet<Token>();
		listeners = new ArrayList<TokenListener>();

		initScheduler();
	}

	private void initScheduler() {
		// one background thread
		scheduledPool = Executors.newScheduledThreadPool(1);


		// fires initially, and then again every 30 seconds
		logger.info("Initializing scheduler...");
		scheduledPool.scheduleWithFixedDelay(new Housekeeping(this), 1, 30, TimeUnit.SECONDS);

		logger.info("Initialized a TokenManager instance");
	}

	/**
	 * Will check to see if token can operate on this path. At the moment just
	 * returns the first available token
	 */
	public Token bestTokenFor(String path) throws OutOfTokensException {
		if (tokens.size() == 0) {
			throw new OutOfTokensException();
		} else {
			Iterator<Token> iter = tokens.iterator();
			while (iter.hasNext()) {
				return iter.next();
			}
		}
		return null;
	}
	
	public void fireTokenEvent(TokenEvent evt) {
		for (TokenListener l : listeners) {
			l.tokenEvent(evt);
		}
	}

	public void fireLoginEvent(Token token) {

		logger.info("Firing successful login event...");
		logger.debug(token.getJson().toString());
		fireTokenEvent(new TokenEvent(this, token, EventEnum.LOGIN));

	}

	public void addTokenListener(TokenListener listener) {
		listeners.add(listener);
	}

	public Auth getAuth() {
		return auth;
	}

	public Set<Token> getManagedTokens() {
		return tokens;
	}
	
	

}
