package com.mockumatrix.toke;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mockumatrix.toke.event.EventEnum;
import com.mockumatrix.toke.event.TokenEvent;
import com.mockumatrix.toke.event.TokenListener;
import com.mockumatrix.toke.exception.OutOfTokensException;

public class TokenManager implements TokenListener {

	private static final Logger logger = LogManager.getLogger(TokenManager.class);
	private Set<Token> tokens;
	
	public TokenManager() {
		tokens = new HashSet<Token>();
		logger.info("Initialized a TokenManager instance");
	}

	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			tokens.add(evt.getToken());
			logger.info("Token with accessor "+evt.getToken().accessor()+" added to TokenManager");
		}
	}
	
	/**
	 * At the moment just returns the first available token
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
