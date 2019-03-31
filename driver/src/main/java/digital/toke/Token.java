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
 * <p>
 * Data Model of a vault token. The token has an accessor and it also knows if
 * it has been instantiated from a successful login. Token objects are not
 * necessarily valid at at any given time.
 * </p>
 * 
 * <p>
 * Token instances are sometimes enriched by a call to lookupToken. This can
 * fail if the caller does not have privileges on that endpoint.
 * </p>
 * 
 * <p>
 * Tokens can have extra data attached in the form of a metadata dictionary.
 * </p>
 * 
 * <p>
 * Token objects also contain a LoginConfig object which was valid at the time
 * they logged in
 * <p/>
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class Token {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Token.class);

	// "expire_time": "2018-05-19T11:35:54.466476215-04:00",
	static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	// the config used for this token if known
	final LoginConfig loginConfig;
	final JSONObject json;
	final boolean fromSuccessfulLoginRequest;

	final JSONObject lookupData;

	final String tokenHandle;

	public Token(LoginConfig lc, JSONObject json, boolean valid) {
		this.loginConfig = lc;
		this.json = json;
		this.fromSuccessfulLoginRequest = valid;
		this.lookupData = new JSONObject();
		this.tokenHandle = defineTokenHandle();
	}

	public Token(LoginConfig lc, JSONObject json, boolean valid, JSONObject lookupData) {
		this.loginConfig = lc;
		this.json = json;
		this.fromSuccessfulLoginRequest = valid;
		this.lookupData = lookupData;
		this.tokenHandle = defineTokenHandle();
	}

	/**
	 * The token handle is a string concatenated from the AuthType, the username (if applicable) and the token accessor. This value is used
	 * as a key in the TokenManager's hash table of managed tokens. 
	 * 
	 * @return
	 */
	protected String defineTokenHandle() {
		StringBuffer buf = new StringBuffer();
		buf.append(loginConfig.authType);
		buf.append("-");
		switch (loginConfig.authType) {
		case TOKEN: {
			if(this.isRoot()) buf.append("root");
			else {
				buf.append(accessor());
			}
			break;
		}
		case APPROLE:
			buf.append(accessor());
			break;
		case LDAP:
			buf.append(loginConfig.username);
			buf.append("-");
			buf.append(accessor());
			break;
		case USERPASS:
			buf.append(loginConfig.username);
			buf.append("-");
			buf.append(accessor());
			break;
		default:
			break;
		}
		return buf.toString();
	}

	public LoginConfig getLoginConfig() {
		return loginConfig;
	}

	public String clientToken() {
		JSONObject auth = json.optJSONObject("auth");
		if (auth == null)
			return "";
		else
			return auth.getString("client_token");
	}

	public String accessor() {
		JSONObject auth = json.optJSONObject("auth");
		if (auth == null)
			return "";
		else
			return auth.getString("accessor");
	}

	public boolean isRenewable() {
		JSONObject auth = json.optJSONObject("auth");
		if (auth == null)
			throw new RuntimeException("Bad data?");
		return auth.optBoolean("renewable", false);
	}

	/**
	 * Return -1 if no period, otherwise return the period (indicates this is a
	 * "periodic" token)
	 * 
	 * @return
	 */
	public int period() {
		JSONObject auth = json.optJSONObject("auth");
		if (auth == null)
			throw new RuntimeException("Bad data?");
		return auth.optInt("period", -1);
	}

	public boolean isPeriodic() {
		return period() != -1;
	}

	public boolean isRoot() {

		JSONObject auth = json.optJSONObject("auth");
		if (auth == null)
			throw new RuntimeException("Bad data?");

		// likely dealing with root. verify by looking for root policy
		boolean isRoot = false;
		JSONArray policyArray = auth.getJSONArray("policies");
		Iterator<Object> iter = policyArray.iterator();
		while (iter.hasNext()) {
			Object item = iter.next();
			if (String.valueOf(item).contains("root")) {
				isRoot = true;
				break;
			}
		}

		return isRoot;
	}

	/**
	 * <p>
	 * Can return null, do not call against root or children of root without a guard. There is some
	 * weirdness about vault does date formats here...
	 * </p>
	 * TODO - review
	 * 
	 * @return
	 */
	public ZonedDateTime expireTime() {

		if (lookupData == null) {
			throw new RuntimeException("lookupData is null - this is likely a programming error as all tokens should have been provisioned with this through a call to lookupToken");
		}

		JSONObject data = lookupData.optJSONObject("data");
		if (data == null)
			throw new RuntimeException("Bad data?");
		Object obj = data.get("expire_time");
		if (obj == null) {
			// would only happen I think with root (?)
			return null;
		} else {
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
		if (fromSuccessfulLoginRequest) {
			return this.clientToken().hashCode();
		} else {
			return getJson().toString().hashCode();
		}
	}

	public JSONObject getLookupData() {
		return lookupData;
	}

	/**
	 * Implementation note - Token equality is used by the TokenManager so this is
	 * important, don't change this unless you know what you are doing. Basically we
	 * are using the actual token value as the comparison point (client_token"
	 * field).
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

		try {
			Token other = (Token) obj;
			if (other.clientToken().equals(clientToken()))
				return true;
		} catch (Exception x) {
		}

		return false;
	}

}
