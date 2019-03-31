/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import org.json.JSONObject;

public abstract class TokeResponseDecorator {

	final Toke toke;

	public TokeResponseDecorator(Toke toke) {
		super();
		this.toke = toke;
	}
	
	public JSONObject json() {
		return toke.accessor().json();
	}
	
}
