/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Data Model of a vault token. The token has an accessor and it also 
 * knows if it has been instantiated from a successful login. Token objects 
 * are not necessarily valid at at any given time.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Token {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Token.class);
	                            
    //  "expire_time": "2018-05-19T11:35:54.466476215-04:00",
	static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	final JSONObject json;
	final boolean fromSuccessfulLoginRequest;

	final JSONObject lookupData;
	
	public Token(JSONObject json, boolean valid) {
		this.json = json;
		this.fromSuccessfulLoginRequest = valid;
		this.lookupData = new JSONObject();
	}
	
	public Token(JSONObject json, boolean valid, JSONObject lookupData) {
		this.json = json;
		this.fromSuccessfulLoginRequest = valid;
		this.lookupData = lookupData;
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
	
	public boolean isRenewable() {
		JSONObject auth = json.optJSONObject("auth");
		if(auth == null) throw new RuntimeException("Bad data?");
		return auth.getBoolean("renewable");
	}
	
	/**
	 * Return -1 if no period, otherwise return the period (indicates this is a "periodic" token)
	 * @return
	 */
	public int period() {
		JSONObject auth = json.optJSONObject("auth");
		if(auth == null) throw new RuntimeException("Bad data?");
		return auth.optInt("period", -1);
	}
	
	public boolean isPeriodic() {
		return period() != -1;
	}
	
	public boolean isRoot() {
		
		JSONObject data = lookupData.optJSONObject("data");
		if(data == null) throw new RuntimeException("Bad data?");
		Object obj = data.get("expire_time");
		if(obj == null) {
			// likely dealing with root. verify by looking for root policy
			boolean isRoot = false;
			JSONArray policyArray = data.getJSONArray("policies");
			Iterator<Object> iter = policyArray.iterator();
			while(iter.hasNext()) {
				Object item = iter.next();
				if(String.valueOf(item).contains("root")) {
					 isRoot = true; break;
				}
			}
			
			return isRoot;
		}
		
		return false;
	}
	
	/**
	 * Can return null, do not call against root without a guard. There is some weirdness about vault date formats here...
	 * 
	 * @return
	 */
	public ZonedDateTime expireTime() {
		JSONObject data = lookupData.optJSONObject("data");
		if(data == null) throw new RuntimeException("Bad data?");
		Object obj = data.get("expire_time");
		if(obj == null) {
			 // would only happen I think with root (?)
			return null;
		}else {
			String base = String.valueOf(obj);
			int stopIndex = base.indexOf('.');
			TemporalAccessor ta = dateFormatter.parse(base.substring(0, stopIndex));
			LocalDateTime d = LocalDateTime.from(ta);
			return d.atZone(ZoneId.systemDefault());
		}
			
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

	public JSONObject getLookupData() {
		return lookupData;
	}

	/**
	 * Implementation note - Token equality is used by the TokenManager 
	 * so this is important, don't change unless you know what you are doing.
	 */
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
