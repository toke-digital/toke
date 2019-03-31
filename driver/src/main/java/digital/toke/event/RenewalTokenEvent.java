/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.event;


import digital.toke.TokenRenewal;

public class RenewalTokenEvent extends TokenEvent {

	private static final long serialVersionUID = 1L;
	
	final EventEnum type = EventEnum.RENEWAL;
	final TokenRenewal item;

	public RenewalTokenEvent(Object arg0, TokenRenewal renewal) {
		super(arg0, EventEnum.RENEWAL);
		this.item = renewal;
	}
	
	public EventEnum getType() {
		return type;
	}

	public TokenRenewal getRenewal() {
		return item;
	}

}
