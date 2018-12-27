package com.mockumatrix.toke;

import com.mockumatrix.toke.response.APIPostResponse;
import com.mockumatrix.toke.response.KVv1APIResponse;
import com.mockumatrix.toke.response.KVv2APIResponse;

/**
 * Simple wrapper on the response out of OKHTTP
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class TokeResponse {

	// the HTTP Response code
	public final int code;

	// if code in range of 200-300 then true
	public final boolean successful;

	// this will contain vault standard error object if successful is false, but
	// should be valid json
	public final String response;

	public TokeResponse(int code, boolean successful, String response) {
		super();
		this.code = code;
		this.successful = successful;

		// if not successful, body might look like this:
		//
		// {"errors": ["ldap operation failed"]}

		this.response = response;

	}
	
	public KVv1APIResponse toKVv1APIResponse() {
		return new KVv1APIResponse(code,successful,response);
	}
	
	public KVv2APIResponse toKVv2APIResponse() {
		return new KVv2APIResponse(code,successful,response);
	}
	
	public APIPostResponse toAPIPostResponse() {
		return new APIPostResponse(code,successful,response);
	}

	
	public String toString() {
		return String.format("TokeResponse: %,%,%", code,successful,response);
	}

}
