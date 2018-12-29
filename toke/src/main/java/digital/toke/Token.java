/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Data Model of a vault token. The token has an accessor and it also 
 * knows if it has been instantiated from a successful login.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Token {

	final JSONObject json;
	final boolean fromSuccessfulLoginRequest; // presumption

	
	public Token(JSONObject json, boolean valid) {
		this.json = json;
		this.fromSuccessfulLoginRequest = valid;
	}

	public String clientToken() {
		JSONObject auth = json.optJSONObject("auth");
		if(auth == null) return "";
		else return auth.getString("client_token");
	}
	
	public String accessor() {
		JSONObject auth = json.optJSONObject("auth");
		if(auth == null) return "";
		else return auth.getString("accessor");
	}
	
	public List<String> errors() {
		
		JSONArray errors = json.optJSONArray("errors");
		List<String> list = new ArrayList<String>();
		errors.forEach(item -> list.add(String.valueOf(item)));
		return list;
	}
	
	public JSONObject getJson() {
		return json;
	}

	public String toString() {
		return json.toString(4);
	}

	public boolean isFromSuccessfulLoginRequest() {
		return fromSuccessfulLoginRequest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fromSuccessfulLoginRequest ? 1231 : 1237);
		result = prime * result + ((json == null) ? 0 : json.toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Token)) {
			return false;
		}
		Token other = (Token) obj;
		if (fromSuccessfulLoginRequest != other.fromSuccessfulLoginRequest) {
			return false;
		}
		if (json == null) {
			if (other.json != null) {
				return false;
			}
		} else if (!json.toString().equals(other.json.toString())) {
			return false;
		}
		return true;
	}

}
