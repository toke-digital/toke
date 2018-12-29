/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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
 * TokenManager looks after token lifecycle and can do auto-renewals, etc. It
 * allows the Driver to continue operations essentially indefinitely, as in an
 * enterprise application which regularly needs to contact vault. 
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class TokenManager implements TokenListener {

	private static final Logger logger = LogManager.getLogger(TokenManager.class);
	private Set<Token> tokens;
	
	private  ScheduledExecutorService scheduledPool;

	
	public TokenManager(Housekeeping housekeeping) {
		
		// concurrent set
		tokens = new ConcurrentSkipListSet<Token>();
		
		// one background thread
		scheduledPool = Executors.newScheduledThreadPool(1);
		
		//fires initially at one second, again every 5 minutes
		housekeeping.setTokens(tokens);
		scheduledPool.scheduleWithFixedDelay(housekeeping, 0, 300, TimeUnit.SECONDS);
		
		logger.info("Initialized a TokenManager instance");

	}
 
	/**
	 * Only adds tokens which are from successful logins. 
	 */
	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			tokens.add(evt.getToken());
			logger.info("Token with accessor "+evt.getToken().accessor()+" added to TokenManager");
		}
	}
	
	/**
	 * Will check to see if token can operate on this path. At the moment just returns the first available token
	 */
	public Token bestTokenFor(String path) throws OutOfTokensException {
		if(tokens.size() == 0) {
			throw new OutOfTokensException();
		}else {
			Iterator<Token> iter = tokens.iterator();
			while(iter.hasNext()) {
				return iter.next();
			}
		}
		return null;
	}

}
