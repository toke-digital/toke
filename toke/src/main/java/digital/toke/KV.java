/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;

/**
 * Base class for shared code in KV implementations
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public abstract class KV extends Base implements TokenListener {

	private static final Logger logger = LogManager.getLogger(KV.class);
	
	protected DriverConfig config;
	protected Token token;
	protected Networking client;
	
	public KV(DriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
	}
	
	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			countDownLatch.countDown();
			logger.info("Token with accessor "+token.accessor()+" set on KV");
		}
		
		// block until we send RELOAD_TOKEN
		if(evt.getType().equals(EventEnum.SET_LATCH)) {
			token = evt.getToken();
			countDownLatch = new CountDownLatch(1);
			latch();
			logger.info("Set latch on a KV instance.");
		}
		
		if(evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
			countDownLatch.countDown();
			logger.info("Reloaded token on a KV instance.");
		}
	}

}
