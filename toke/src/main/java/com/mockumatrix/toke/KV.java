package com.mockumatrix.toke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mockumatrix.toke.event.EventEnum;
import com.mockumatrix.toke.event.TokenEvent;
import com.mockumatrix.toke.event.TokenListener;

public abstract class KV implements TokenListener {

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
			logger.info("Token with accessor "+token.accessor()+" set on KV");
		}
	}

}
