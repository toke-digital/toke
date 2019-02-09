/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

import java.util.List;

import digital.toke.Token;

/**
 * Thrown when login fails for a network-oriented reason or some other reason releated to the attempt.
 * See 
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class LoginFailedException extends TokeException {

	private static final long serialVersionUID = 1L;
	
	Token badToken;

	public LoginFailedException() {}
	
	public LoginFailedException(Token badToken) {
		this.badToken = badToken;
	}

	public LoginFailedException(String arg0) {
		super(arg0);
		
	}

	public LoginFailedException(Throwable arg0) {
		super(arg0);
	}

	public Token getBadToken() {
		return badToken;
	}
	
	public List<String> errors() {
		return badToken.errors();
	}
}
