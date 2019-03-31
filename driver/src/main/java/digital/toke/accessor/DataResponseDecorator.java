/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Use with KVv1 and KVv2 read operations
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class DataResponseDecorator extends TokeResponseDecorator {
	

	public DataResponseDecorator(Toke resp) {
		super(resp);
	}
	
	public Map<String,Object> map() {
		
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		
		JSONObject top = json();
		JSONObject data = top.optJSONObject("data");
		if(data == null) {
			return map;
		}else {
			JSONObject inner = data.optJSONObject("data");
			if(inner != null) {
				data = inner;
			}
		}
		Iterator<String> keys = data.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			map.put(key, data.get(key));
		}
		
		return map;
	}
	
	/**
	 * Use on KVv1 and KVv2 reads. Attempt to preserve order 
	 * (not sure if the results are ordered yet, but may be, if so we will support that)
	 * 
	 * @return
	 */
	public Map<String,Object> metadata() {
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		
		JSONObject top = json();
		JSONObject data = top.optJSONObject("data");
		if(data == null) {
			return map;
		}else {
			JSONObject inner = data.optJSONObject("metadata");
			if(inner != null) {
				data = inner;
			}
		}
		Iterator<String> keys = data.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			map.put(key, data.get(key));
		}
		
		return map;
	}
	

}
