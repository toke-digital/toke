/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

public class TokenRenewal {

	public final String handle;
	public final Token oldToken;
	public final Token newToken;
	public final RenewalType renewalType;
	
	public TokenRenewal(String handle, RenewalType type, Token oldToken, Token newToken) {
		super();
		this.handle = handle;
		this.oldToken = oldToken;
		this.newToken = newToken;
		this.renewalType = type;
	}
	
	
	
}
