/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;


/**
 * <p>TokenManager looks after token life-cycle and can do auto-renewals, etc. It
 * allows the Driver to continue operations essentially indefinitely, as in an
 * enterprise application which regularly needs to contact vault.</p>
 * 
 * <p>Tokens have a metadata dictionary which must contain a "token_handle" with value like "root" or "bob". This handle
 * allows us to easily distinguish and apply a token</p> 
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class TokenManager {

	private static final Logger logger = LogManager.getLogger(TokenManager.class);

	private final TokeDriverConfig driverConfig;
	
	private final Auth auth;
	private final Map<String,Token> tokens;
	private final List<TokenListener> listeners;

	private ScheduledExecutorService scheduledPool;

	/**
	 * The constructor will cause the run() method to fire on a background thread. This will initiate the chain of actions to make the driver usable based on the config
	 * 
	 * @param auth
	 */
	public TokenManager(TokeDriverConfig config, Auth auth) {
		this.driverConfig = config;
		this.auth = auth;
		tokens = new HashMap<String,Token>();
		listeners = new ArrayList<TokenListener>();
	}

	public void initScheduler(DefaultHousekeepingImpl impl) {
		// one background thread
		scheduledPool = Executors.newScheduledThreadPool(1);


		// fires initially, and then again every 30 seconds
		logger.info("Initializing scheduler...");
		scheduledPool.scheduleWithFixedDelay(impl, 0, 30, TimeUnit.SECONDS);

		logger.info("Initialized a TokenManager instance");
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
	
	public void updateManagedTokens(List<TokenRenewal> list) {
		// TODO
	}

	public Auth getAuth() {
		return auth;
	}
	
	public TokeDriverConfig getDriverConfig() {
		return driverConfig;
	}

	Map<String, Token> getTokens() {
		return tokens;
	}
	
	

}
