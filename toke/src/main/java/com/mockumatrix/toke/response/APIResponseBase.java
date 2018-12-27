package com.mockumatrix.toke.response;

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
		
		@Override
		public String toString() {
			return "APIResponse [code=" + code + ", successful=" + successful + ", response=" + response +"]";
		}

}
