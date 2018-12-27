package com.mockumatrix.toke.response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;


public class KVv2APIResponse extends APIResponseBase implements APIResponse {
	
	public KVv2APIResponse(int code, boolean successful, String response) {
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
	
	/* (non-Javadoc)
	 * @see com.mockumatrix.toke.APIResponse#metadata()
	 */
	@Override
	public Map<String,Object> metadata() {
		Map<String,Object> map = new HashMap<String, Object>();
		
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
