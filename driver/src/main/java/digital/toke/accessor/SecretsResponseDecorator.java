/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import java.util.ArrayList;
import java.util.List;

public class SecretsResponseDecorator extends TokeResponseDecorator {

	public SecretsResponseDecorator(Toke resp) {
		super(resp);
	}
	
	public List<String> secrets() {
		List<Object> list = json().getJSONObject("data").getJSONArray("keys").toList();
		List<String> newList = new ArrayList<String>();
		list.forEach(item-> newList.add(String.valueOf(item)));
		return newList;
	}

}
