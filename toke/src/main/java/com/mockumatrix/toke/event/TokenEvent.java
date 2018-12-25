package com.mockumatrix.toke.event;

import java.util.EventObject;

import com.mockumatrix.toke.Token;

public class TokenEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	final EventEnum type;
	final Token token;
	
	public TokenEvent(Object obj, EventEnum type) {
		super(obj);
		this.type = type;
		token = null;
	}
	
	public TokenEvent(Object obj, Token token, EventEnum type) {
		super(obj);
		this.type = type;
		this.token = token;
	}

	public Token getToken() {
		return token;
	}

	public EventEnum getType() {
		return type;
	}

}
