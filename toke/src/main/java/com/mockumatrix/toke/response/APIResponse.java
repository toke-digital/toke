package com.mockumatrix.toke.response;

import java.util.Map;

import org.json.JSONObject;

public interface APIResponse {

	JSONObject json();

	Map<String, Object> data();

	Map<String, Object> metadata();

}