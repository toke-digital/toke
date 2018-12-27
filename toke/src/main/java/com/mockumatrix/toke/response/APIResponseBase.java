package com.mockumatrix.toke.response;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public abstract class APIResponseBase implements APIResponse {

	// the HTTP Response code
		public final int code;
		
		// if code in range of 200-300 then true
		public final boolean successful;
		
		// this will contain vault standard error object if successful is false, but should be valid json
		public final String response;
		
		
		protected JSONObject json;
		
		public APIResponseBase(int code, boolean successful, String response) {
			super();
			this.code = code;
			this.successful = successful;
			
			//if not successful, body might look like this:
			//
			// {"errors": ["ldap operation failed"]}
			
			this.response = response;
		
		}
		
		@Override
		public JSONObject json() {
			if(json == null) {
				json = new JSONObject(response);
			}
			
			return json;
		}
		
		/**
		 * Used only with 'list' endpoints
		 */
		@Override
		public List<String> keys() {
			List<Object> list = json().getJSONObject("data").getJSONArray("keys").toList();
			List<String> newList = new ArrayList<String>();
			list.forEach(item-> newList.add(String.valueOf(item)));
			return newList;
		}
		
		@Override
		public String toString() {
			return "APIResponse [code=" + code + ", successful=" + successful + ", response=" + response +"]";
		}

}
