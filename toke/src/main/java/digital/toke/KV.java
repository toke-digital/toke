/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.event.EventEnum;
import digital.toke.event.RenewalTokenEvent;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;

/**
 * Base class for shared code in KV implementations
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public abstract class KV extends ServiceBase implements TokenListener {

	private static final Logger logger = LogManager.getLogger(KV.class);
	
	protected TokeDriverConfig config;
	protected Token token;
	protected Networking client;
	
	public KV(TokeDriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
	}
	
	@Override
	public void tokenEvent(TokenEvent evt) {
		
		if(evt.getType().equals(EventEnum.RENEWAL)) {
			RenewalTokenEvent thisEvt = (RenewalTokenEvent) evt;
			for(TokenRenewal tr : thisEvt.getList()) {
				// update only what had been sent previously
				if(tr.oldToken.clientToken().equals(this.token.clientToken())) {
					this.token = tr.newToken;
					break;
				}
			}
			logger.info("Token with accessor "+token.accessor()+" set on Networking instance");
			return;
		}
		
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			countDown();
			logger.info("Token with accessor "+token.accessor()+" set on "+this.getClass().getName());
			return;
		}
		
	//	if(evt.getType().equals(EventEnum.SET_LATCH)) {
	//		refreshLatch();
	//		latch();
	//		logger.info("Reloaded token on "+this.getClass().getName());
	//	}
		
		if(evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
		//    countDown();
			logger.info("Reloaded token on "+this.getClass().getName());
			return;
		}
	}
	
	

}
