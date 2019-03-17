/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.HttpUrl;

/**
 * Uses a fluent idiom to allow configuration; most items have sensible defaults.
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class TokeDriverConfig {

	private static final Logger logger = LogManager.getLogger(TokeDriverConfig.class);
	
	private TokeDriverConfig() {}

	HousekeepingConfig housekeepingConfig;

	// auth
	AuthType authType; // e.g., supports TOKEN, LDAP, APPROLE, USERPASS;

	// set based on selected auth type
	String token;
	File tokenFile;

	String secretId;
	String roleId;

	// used with LDAP and USERPASS
	String username;
	String password;

	// pathing stuff
	String host; // e.g., localhost
	String proto; // e.g., https
	int port; // e.g., 8200
	String vaultApiPrefix; // e.g., /v1
	String defaultKVv1Name; // e.g., /secret
	String defaultKVv2Name; // e.g., /secret

	String kv1Name; // the user-configured kv name, e.g., /other
	String kv2Name; // the user-configured kv2 name, e.g., /other2

	String authPath; // if auth is not located at /auth, such as /auth-special

	// login token parameter
	boolean renewable;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		/**
		 * Reasonable defaults -
		 */
		public Builder() {

			vaultApiPrefix = "/v1";
			port = -1;
			proto = "https";
			host = "127.0.0.1";
			defaultKVv1Name = "/secret";
			defaultKVv2Name = "/secret";

			authPath = "/auth";
		}

		HousekeepingConfig housekeepingConfig;

		// auth
		AuthType authType; // e.g., supports TOKEN, LDAP, APPROLE, USERPASS;

		// set based on selected auth type
		String token;
		File tokenFile;

		String secretId;
		String roleId;

		// used with LDAP and USERPASS
		String username;
		String password;

		// pathing stuff
		String host; // e.g., localhost
		String proto; // e.g., https
		int port; // e.g., 8200
		String vaultApiPrefix; // e.g., /v1
		String defaultKVv1Name; // e.g., /secret
		String defaultKVv2Name; // e.g., /secret

		String kv1Name; // the user-configured kv name, e.g., /other
		String kv2Name; // the user-configured kv2 name, e.g., /other2

		String authPath; // if auth is not located at /auth, such as /auth-special

		// login token parameter
		boolean renewable;

		public Builder host(String val) {
			host = val;
			return this;
		}

		public Builder proto(String val) {
			proto = val;
			return this;
		}

		public Builder port(int val) {
			port = val;
			return this;
		}

		public Builder vaultApiPrefix(String val) {
			vaultApiPrefix = val;
			return this;
		}

		public Builder defaultKVv1Name(String val) {
			defaultKVv1Name = val;
			return this;
		}

		public Builder defaultKVv2Name(String val) {
			defaultKVv2Name = val;
			return this;
		}

		public Builder authType(String val) {
			authType = AuthType.valueOf(val.toUpperCase());
			return this;
		}

		public Builder authType(AuthType type) {
			authType = type;
			return this;
		}

		public Builder token(String val) {
			token = val;
			return this;
		}

		public Builder renewable(boolean b) {
			renewable = b;
			return this;
		}

		public Builder secretId(String val) {
			secretId = val;
			return this;
		}

		public Builder roleId(String val) {
			roleId = val;
			return this;
		}

		public Builder username(String val) {
			username = val;
			return this;
		}

		public Builder password(String val) {
			password = val;
			return this;
		}

		public Builder authPath(String val) {
			authPath = val;
			return this;
		}

		public Builder kvName(String val) {
			kv1Name = val;
			return this;
		}

		public Builder kv2Name(String val) {
			kv2Name = val;
			return this;
		}

		public Builder tokenFile(File fileWithToken) {
			tokenFile = fileWithToken;
			return this;
		}

		public TokeDriverConfig build() {
			TokeDriverConfig config = new TokeDriverConfig();
			config.housekeepingConfig = this.housekeepingConfig;
			config.authType = this.authType;
			config.token = this.token;
			config.tokenFile = this.tokenFile;
			config.secretId = this.secretId;
			config.roleId = this.roleId;
			config.username = this.username;
			config.password = this.password;
			config.host = this.host;
			config.proto = this.proto;
			config.port = this.port;
			config.vaultApiPrefix = this.vaultApiPrefix;
			config.defaultKVv1Name = this.defaultKVv1Name;
			config.defaultKVv2Name = this.defaultKVv2Name;
			config.kv1Name = this.kv1Name;
			config.kv2Name = this.kv2Name;
			config.authPath = this.authPath;
			config.renewable = this.renewable;

			return config;
		}

		public Builder housekeepingConfig(HousekeepingConfig housekeepingConfig) {
			this.housekeepingConfig = housekeepingConfig;
			return this;
		}

	}

	// constants for KVv2 - can use static import for these
	static final String KVv2CONFIG = "/config";
	static final String KVv2DATA = "/data";
	static final String KVv2DELETE = "/delete";
	static final String KVv2UNDELETE = "/undelete";
	static final String KVv2REMOVE = "/remove";
	static final String KVv2METADATA = "/metadata";
	static final String KVv2DESTROY = "/destroy";

	public StringBuffer baseURL() {
		StringBuffer buf = new StringBuffer();
		buf.append(proto);
		buf.append("://");
		buf.append(host);
		if (port != -1) {
			buf.append(":");
			buf.append(port);
		}
		buf.append(vaultApiPrefix);

		return buf;
	}

	// AUTH

	public String authLdapLogin() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/ldap/login/");
		buf.append(username);
		return buf.toString();
	}

	public String authAppRoleLogin() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/approle/login");
		return buf.toString();
	}

	public String authUserPassLogin() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/userpass/login/");
		buf.append(username);
		return buf.toString();
	}

	public String authTokenLogin() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/token/create");
		return buf.toString();
	}

	public String authTokenLookup() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/token/lookup");
		return buf.toString();
	}

	public String authTokenLookupSelf() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/token/lookup-self");
		return buf.toString();
	}

	public String authTokenRenew() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/token/renew");
		return buf.toString();
	}

	public String authTokenRenewSelf() {
		StringBuffer buf = baseURL();
		buf.append(authPath);
		buf.append("/token/renew-self");
		return buf.toString();
	}

	// KVv2

	public String kv2Config() {
		StringBuffer buf = baseURL();
		if (kv2Name == null) {
			buf.append(this.defaultKVv2Name);
		} else {
			if (!kv2Name.startsWith("/"))
				buf.append("/");
			buf.append(kv2Name);
		}
		buf.append("/config");
		return buf.toString();
	}

	public String kv2Path(String verb, String path) {

		StringBuffer buf = baseURL();
		if (kv2Name == null) {
			buf.append(this.defaultKVv2Name);
		} else {
			if (!kv2Name.startsWith("/"))
				buf.append("/");
			buf.append(kv2Name);
		}
		buf.append(verb);

		if (path != null) {
			if (path.charAt(0) != '/') {
				buf.append("/");
			}
			buf.append(path);
		}

		return buf.toString();
	}

	/**
	 * Special case for list!
	 * 
	 * @param path
	 * @return
	 */
	public HttpUrl kv2List(String path) {

		StringBuffer segments = new StringBuffer("v1");
		if (kv2Name == null) {
			segments.append(this.defaultKVv2Name);
		} else {
			if (!kv2Name.startsWith("/"))
				segments.append("/");
			segments.append(kv2Name);
		}

		segments.append(KVv2METADATA);

		if (path != null) {
			if (path.charAt(0) != '/') {
				segments.append("/");
			}
			segments.append(path);
		}

		HttpUrl url = new HttpUrl.Builder().scheme(proto).host(host).port(port).addPathSegments(segments.toString())
				.addQueryParameter("list", "true").build();

		return url;
	}

	// KVv1

	public String kv1Path(String path) {

		StringBuffer buf = baseURL();
		if (kv1Name == null) {
			buf.append(this.defaultKVv1Name);
		} else {
			if (!kv1Name.startsWith("/"))
				buf.append("/");
			buf.append(kv1Name);
		}

		if (path != null) {
			if (path.charAt(0) != '/') {
				buf.append("/");
			}
			buf.append(path);
		}

		return buf.toString();
	}

	public HttpUrl kv1List(String path) {

		StringBuffer segments = new StringBuffer("v1");
		if (kv1Name == null) {
			segments.append(this.defaultKVv1Name);
		} else {
			if (!kv1Name.startsWith("/"))
				segments.append("/");
			segments.append(kv1Name);
		}

		if (path != null) {
			if (path.charAt(0) != '/') {
				segments.append("/");
			}
			segments.append(path);
		}

		HttpUrl url = new HttpUrl.Builder().scheme(proto).host(host).port(port).addPathSegments(segments.toString())
				.addQueryParameter("list", "true").build();

		return url;
	}
	
	public HttpUrl approleList(String path) {

		StringBuffer segments = new StringBuffer("v1");

		if (path != null) {
			if (path.charAt(0) != '/') {
				segments.append("/");
			}
			segments.append(path);
		}

		HttpUrl url = new HttpUrl.Builder().scheme(proto).host(host).port(port).addPathSegments(segments.toString())
				.addQueryParameter("list", "true").build();

		return url;
	}

	/**
	 * Used with initial token-based authentication, e.g., typically a root token.
	 * This initial login token is always used to acquire a child token for the
	 * actual service calls - it will never be used directly for api calls
	 * 
	 * @return
	 */
	public String findToken() {

		if (tokenFile != null) {
			try {
				String t = new String(Files.readAllBytes(tokenFile.toPath()), "UTF-8");
				logger.debug("returning a token from file to use for auth");
				return t;
			} catch (IOException e) {
				logger.error("Failed to read file with token: " + tokenFile.getPath().toString());
				logger.error(e);
			}
		} else if (token == null) {
			logger.error("Token asked for but not found in config, please fix this and try again.");
		} else {
			logger.debug("returning token from config, not file.");
			return token;
		}

		return null;
	}

	public HousekeepingConfig getHousekeepingConfig() {
		return housekeepingConfig;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authPath == null) ? 0 : authPath.hashCode());
		result = prime * result + ((authType == null) ? 0 : authType.hashCode());
		result = prime * result + ((defaultKVv1Name == null) ? 0 : defaultKVv1Name.hashCode());
		result = prime * result + ((defaultKVv2Name == null) ? 0 : defaultKVv2Name.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((housekeepingConfig == null) ? 0 : housekeepingConfig.hashCode());
		result = prime * result + ((kv1Name == null) ? 0 : kv1Name.hashCode());
		result = prime * result + ((kv2Name == null) ? 0 : kv2Name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result + ((proto == null) ? 0 : proto.hashCode());
		result = prime * result + (renewable ? 1231 : 1237);
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result + ((secretId == null) ? 0 : secretId.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((tokenFile == null) ? 0 : tokenFile.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((vaultApiPrefix == null) ? 0 : vaultApiPrefix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokeDriverConfig other = (TokeDriverConfig) obj;
		if (authPath == null) {
			if (other.authPath != null)
				return false;
		} else if (!authPath.equals(other.authPath))
			return false;
		if (authType != other.authType)
			return false;
		if (defaultKVv1Name == null) {
			if (other.defaultKVv1Name != null)
				return false;
		} else if (!defaultKVv1Name.equals(other.defaultKVv1Name))
			return false;
		if (defaultKVv2Name == null) {
			if (other.defaultKVv2Name != null)
				return false;
		} else if (!defaultKVv2Name.equals(other.defaultKVv2Name))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (housekeepingConfig == null) {
			if (other.housekeepingConfig != null)
				return false;
		} else if (!housekeepingConfig.equals(other.housekeepingConfig))
			return false;
		if (kv1Name == null) {
			if (other.kv1Name != null)
				return false;
		} else if (!kv1Name.equals(other.kv1Name))
			return false;
		if (kv2Name == null) {
			if (other.kv2Name != null)
				return false;
		} else if (!kv2Name.equals(other.kv2Name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (proto == null) {
			if (other.proto != null)
				return false;
		} else if (!proto.equals(other.proto))
			return false;
		if (renewable != other.renewable)
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (secretId == null) {
			if (other.secretId != null)
				return false;
		} else if (!secretId.equals(other.secretId))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (tokenFile == null) {
			if (other.tokenFile != null)
				return false;
		} else if (!tokenFile.equals(other.tokenFile))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (vaultApiPrefix == null) {
			if (other.vaultApiPrefix != null)
				return false;
		} else if (!vaultApiPrefix.equals(other.vaultApiPrefix))
			return false;
		return true;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
