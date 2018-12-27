package com.mockumatrix.toke.response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

/**
 * It's necessary to get the body of the response as the OKHttp Response needs to be closed;
 * so we put the contents into this primitive binding
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class KVv1APIResponse extends APIResponseBase implements APIResponse {

	
	public KVv1APIResponse(int code, boolean successful, String response) {
		super(code,successful,response);
	}
	
	@Override
	public Map<String,Object> data() {
		Map<String,Object> map = new HashMap<String, Object>();
		
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
	
	@Override
	public Map<String,Object> metadata() {
		Map<String,Object> map = new HashMap<String, Object>();
		
		JSONObject top = json();
		Iterator<String> keys = top.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			if(key.equals("data")) continue;
			map.put(key, top.get(key));
		}
		
		return map;
	}

}
