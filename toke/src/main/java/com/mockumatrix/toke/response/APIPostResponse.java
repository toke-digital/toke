package com.mockumatrix.toke.response;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class APIPostResponse extends APIResponseBase implements APIResponse {

	private static final JSONObject json = new JSONObject();
	private static final Map<String,Object> map = new HashMap<String,Object>();
	
	public APIPostResponse(int code, boolean successful, String response) {
		super(code, successful, response);
	}

	@Override
	public JSONObject json() {
		return json;
	}

	@Override
	public Map<String, Object> data() {
		return map;
	}

	@Override
	public Map<String, Object> metadata() {
		return map;
	}

}
