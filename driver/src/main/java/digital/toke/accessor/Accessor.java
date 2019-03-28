/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * Parent class of Accessors. These are sort of a cross between a Decorator and an Adapter pattern design.
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class Accessor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Accessor.class);

	public final Toke toke;
	private JSONObject object;

	public Accessor(Toke resp) {
		this.toke = resp;
	}
	
	public JSONObject json() {
		if(object == null) object = new JSONObject(toke.response);
		return object;
	}
	
	public String toString() {
		return json().toString(4);
	}
}
