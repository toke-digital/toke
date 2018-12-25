package com.mockumatrix.toke;

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
public class APIResponse {

	// the HTTP Response code
	public final int code;
	
	// if code in range of 200-300 then true
	public final boolean successful;
	
	// this will contain vault standard error object if successful is false, but should be valid json
	public final String response;
	
	
	private JSONObject json;
	
	public APIResponse(int code, boolean successful, String response) {
		super();
		this.code = code;
		this.successful = successful;
		
		//if not successful, body might look like this:
		//
		// {"errors": ["ldap operation failed"]}
		
		this.response = response;
	
	}
	
	public JSONObject json() {
		if(json == null) {
			System.err.println(response);
			json = new JSONObject(response);
		}
		
		return json;
	}
	
	// read the 'data' portion of a response into a map
	public Map<String,Object> data() {
		Map<String,Object> map = new HashMap<String, Object>();
		JSONObject top = json();
		JSONObject data = top.getJSONObject("data").getJSONObject("data");
		Iterator<String> keys = data.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			map.put(key, data.get(key));
		}
		
		return map;
	}
	
	public Map<String,Object> metadata() {
		Map<String,Object> map = new HashMap<String, Object>();
		JSONObject top = json();
		JSONObject data = top.getJSONObject("data").getJSONObject("metadata");
		Iterator<String> keys = data.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			map.put(key, data.get(key));
		}
		
		return map;
	}

	@Override
	public String toString() {
		return "APIResponse [code=" + code + ", successful=" + successful + ", response=" + response +"]";
	}
	
	

}
