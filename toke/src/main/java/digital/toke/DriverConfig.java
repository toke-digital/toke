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

/**
 * Uses a fluent idiom to allow configuration; most items have sensible defaults.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class DriverConfig {
	
	private static final Logger logger = LogManager.getLogger(DriverConfig.class);
	
	// auth
	AuthType authType; //        e.g., supports TOKEN, LDAP, APPROLE, USERPASS;
	
	// set based on selected auth type
	String token;
	File tokenFile;
	
	String secretId;
	String roleId;
	
	// used with LDAP and USERPASS
	String username;
	String password;
	
	// pathing stuff
	String host; // 			e.g., localhost
	String proto; //            e.g., https
	int port; //                e.g., 8200
	String vaultApiPrefix; //   e.g., /v1
	String defaultKVv1Name; //  e.g., /secret
	String defaultKVv2Name; //  e.g., /secret
	
	String kv1Name; // the user-configured kv name, e.g., /other
	String kv2Name; // the user-configured kv2 name, e.g., /other2
	
	String authPath; // if auth is not located at /auth, such as /auth-special
	
	// login token parameter
	boolean renewable;
	
	// constants for KVv2 - can use static import for these
	static final String KVv2CONFIG = "/config"; 
	static final String KVv2DATA = "/data"; 
	static final String KVv2DELETE = "/delete"; 
	static final String KVv2UNDELETE = "/undelete";
	static final String KVv2REMOVE = "/remove";
	static final String KVv2METADATA = "/metadata";
	static final String KVv2DESTROY = "/destroy";
	
	/**
	 * Sets to default values
	 */
	public DriverConfig() {
		vaultApiPrefix = "/v1";
		port = -1;
		proto = "https";
		host= "127.0.0.1";
		defaultKVv1Name = "/secret";
		defaultKVv2Name = "/secret";
		
		authPath = "/auth";
		renewable = true;
	}
	
	public StringBuffer baseURL() {
		StringBuffer buf = new StringBuffer();
		buf.append(proto);
		buf.append("://");
		buf.append(host);
		if(port != -1) {
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
	
	// KVv2
	
	public String kv2Config() {
		StringBuffer buf = baseURL();
		if(kv2Name == null) {
			buf.append(this.defaultKVv2Name);
		}else {
			buf.append(kv2Name);
		}
		buf.append("/config");
		return buf.toString();
	}
	
	public String kv2Path(String verb, String path) {
		
		StringBuffer buf = baseURL();
		if(kv2Name == null) {
			buf.append(this.defaultKVv2Name);
		}else {
			buf.append(kv2Name);
		}
		buf.append(verb);
		
		if(path != null) {
			if(path.charAt(0) != '/') {
				buf.append("/");
			}
			buf.append(path);
		}
		
		return buf.toString();
	}
	
	// KVv1
	
	public String kv1Path(String path) {
		
		StringBuffer buf = baseURL();
		if(kv1Name == null) {
			buf.append(this.defaultKVv1Name);
		}else {
			buf.append(kv1Name);
		}
		
		if(path != null) {
			if(path.charAt(0) != '/') {
				buf.append("/");
			}
			buf.append(path);
		}
		
		return buf.toString();
	}
	
	
	public DriverConfig host(String val) {
		host = val;
		return this;
	}
	
	public DriverConfig proto(String val) {
		proto = val;
		return this;
	}
	
	public DriverConfig port(int val) {
		port = val;
		return this;
	}
	
	public DriverConfig vaultApiPrefix(String val) {
		vaultApiPrefix = val;
		return this;
	}
	
	public DriverConfig defaultKVv1Name(String val) {
		defaultKVv1Name = val;
		return this;
	}
	
	public DriverConfig defaultKVv2Name(String val) {
		defaultKVv2Name = val;
		return this;
	}
	
	public DriverConfig authType(String val) {
		authType = AuthType.valueOf(val.toUpperCase());
		return this;
	}
	
	public DriverConfig token(String val) {
		token = val;
		return this;
	}
	
	public DriverConfig renewable(boolean b) {
		renewable = b;
		return this;
	}
	
	public DriverConfig secretId(String val) {
		secretId = val;
		return this;
	}
	
	public DriverConfig roleId(String val) {
		roleId = val;
		return this;
	}
	
	public DriverConfig username(String val) {
		username = val;
		return this;
	}
	
	public DriverConfig password(String val) {
		password = val;
		return this;
	}
	
	public DriverConfig authPath(String val) {
		authPath = val;
		return this;
	}
	
	public DriverConfig kvName(String val) {
		kv1Name = val;
		return this;
	}
	
	public DriverConfig kv2Name(String val) {
		kv2Name = val;
		return this;
	}
	
	public DriverConfig tokenFile(File fileWithToken) {
		tokenFile = fileWithToken;
		return this;
	}
	
	public String findToken() {
		
		if(tokenFile != null) {
			try {
				String t =  new String(Files.readAllBytes(tokenFile.toPath()), "UTF-8");
				logger.debug("returning a token from file to use for auth");
				return t;
			} catch (IOException e) {
				logger.error("Failed to read file with token: "+tokenFile.getPath().toString());
				logger.error(e);
			}
		} else if(token == null) {
			logger.error("Token asked for but not found in config, please fix this and try again.");
		} else {
			logger.debug("returning token from config, not file.");
			return token;
		}
		
		return null;
	}

}
