/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import org.json.JSONObject;

/**
 * Use with KVv1 and KVv2 read operations
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class PolicyResponseDecorator extends TokeResponseDecorator {
	
	public String name, rules;

	public PolicyResponseDecorator(Toke resp) {
		super(resp);
		JSONObject top = json();
		name = top.optString("name");
		rules = top.optString("rules");
	}
	
}
