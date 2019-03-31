/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

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

	LoginConfig loginConfig;
	HousekeepingConfig housekeepingConfig;

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
		LoginConfig loginConfig;

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

		public Builder renewable(boolean b) {
			renewable = b;
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

		public TokeDriverConfig build() {
			TokeDriverConfig config = new TokeDriverConfig();
			config.housekeepingConfig = this.housekeepingConfig;
			config.loginConfig = this.loginConfig;
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
		
		public Builder loginConfig(LoginConfig loginConfig) {
			this.loginConfig = loginConfig;
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
		buf.append(loginConfig.username);
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
		buf.append(loginConfig.username);
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

		if (loginConfig.tokenFile != null) {
			try {
				String t = new String(Files.readAllBytes(loginConfig.tokenFile.toPath()), "UTF-8");
				logger.debug("returning a token from file to use for auth");
				return t;
			} catch (IOException e) {
				logger.error("Failed to read file with token: " + loginConfig.tokenFile.getPath().toString());
				logger.error(e);
			}
		} else if (loginConfig.token == null) {
			logger.error("Token asked for but not found in config, please fix this and try again.");
		} else {
			logger.debug("returning token from config, not file.");
			return loginConfig.token;
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
		return Objects.hash(authPath, defaultKVv1Name, defaultKVv2Name, host, housekeepingConfig, kv1Name, kv2Name,
				loginConfig, port, proto, renewable, vaultApiPrefix);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TokeDriverConfig)) {
			return false;
		}
		TokeDriverConfig other = (TokeDriverConfig) obj;
		return Objects.equals(authPath, other.authPath) && Objects.equals(defaultKVv1Name, other.defaultKVv1Name)
				&& Objects.equals(defaultKVv2Name, other.defaultKVv2Name) && Objects.equals(host, other.host)
				&& Objects.equals(housekeepingConfig, other.housekeepingConfig)
				&& Objects.equals(kv1Name, other.kv1Name) && Objects.equals(kv2Name, other.kv2Name)
				&& Objects.equals(loginConfig, other.loginConfig) && port == other.port
				&& Objects.equals(proto, other.proto) && renewable == other.renewable
				&& Objects.equals(vaultApiPrefix, other.vaultApiPrefix);
	}

	

}
