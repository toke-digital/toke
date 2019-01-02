/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import digital.toke.accessor.Toke;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.LoginFailedException;
import digital.toke.exception.ReadException;

/**
 * The auth module implements a vault login using various auth types such as LDAP and APPROLE. 
 * Tokens are sent wrapped in events to interested parties
 * 
 * See https://www.vaultproject.io/docs/auth/
 * 
 * @author Dave
 *
 */
public class Auth {

	private static final Logger logger = LogManager.getLogger(Auth.class);
	
	DriverConfig config;
	Networking client;
	
	public Auth(DriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		logger.info("Auth instance "+this.hashCode()+" configured");
	}
	
	
	public void logoff(Token token) {
		// destroy token TODO
	}
	
	public void logoffSelf() {
		// destroy token TODO
	}
	
	public Toke checkSealStatus() throws ReadException {

		String url = config.baseURL().append("/sys/seal-status").toString();

		try {
			return client.get(url,false);
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	/**
	 * Unseal for a set of keys. Return the last response
	 * 
	 * @param keys
	 * @param reset
	 * @param migrate
	 * @return Toke
	 * @throws ConfigureException
	 */
	public Toke unseal(List<String> keys, boolean reset, boolean migrate) throws ConfigureException {
		Toke toke = null;
		for(String key: keys) {
			toke = unseal(key, reset, migrate);
		}
		return toke;
	}

	public Toke unseal(String key, boolean reset, boolean migrate) throws ConfigureException {

		String url = config.baseURL().append("/sys/unseal").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("key", key).put("reset", reset).put("migrate", migrate);

		logger.debug(json.toString(4));

		try {
			Toke response = client.put(url, json.toString(), false);
			// we expect a 200 per the documentation
			if(response.code!= 200) throw new ConfigureException(response.toString());
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	
	// Logins. All logins are POSTs
	
	Token loginLDAP() throws LoginFailedException {
		String url = config.authLdapLogin();
		JSONObject json = new JSONObject();
		json.put("password", config.password);
		return httpLogin(url,json);
	}
	
	/**
	 * Assumption: bind_secret_id is true for this app role, which forces the secret_id to be used
	 * 
	 * @throws LoginFailedException
	 */
    Token loginAppRole() throws LoginFailedException {
		String url = config.authAppRoleLogin();
		JSONObject json = new JSONObject();
		json.put("role_id", config.roleId);
		json.put("secret_id", config.secretId);
		return  httpLogin(url,json);
	}
    
    Token loginUserPass() throws LoginFailedException {
    	logger.debug("in loginUserPass");
  		String url = config.authUserPassLogin();
  		JSONObject json = new JSONObject();
  		json.put("password", config.password);
  		return httpLogin(url,json);
  	}
    
    Token loginToken() throws LoginFailedException {
  		String url = config.authTokenLogin();
  		JSONObject json = new JSONObject();
  		// TODO at the moment only supporting one config property here
  		json.put("renewable", config.renewable);
  		Toke result = null;
    	try {
  			result = client.loginToken(url, json.toString(), config.findToken());
  		} catch (IOException e) {
  			throw new LoginFailedException(e);
  		}
    	
    	return new Token(new JSONObject(result.response), result.successful);
		
    }
    
    Token loginToken(CreateTokenParameters params) throws LoginFailedException {
  		String url = config.authTokenLogin();
  		JSONObject json = new JSONObject();
  		// TODO at the moment only supporting one config property here
  		json.put("renewable", config.renewable);
  		Toke result = null;
    	try {
  			result = client.loginToken(url, json.toString(), config.findToken());
  		} catch (IOException e) {
  			throw new LoginFailedException(e);
  		}
    	
    	return new Token(new JSONObject(result.response), result.successful);
		
    }
    
    private Token httpLogin(String url, JSONObject json) throws LoginFailedException {
    	
    	logger.debug("in httpLogin");
    	Toke result = null;
    	try {
  			result = client.login(url, json.toString());
  			logger.debug("got result: "+result);
  		} catch (IOException e) {
  			throw new LoginFailedException(e);
  		}
    	return new Token(new JSONObject(result.response), result.successful);
    }
    
	/**
	 * Currently implementing LDAP, APPROLE, USERPASS, TOKEN
	 * 
	 * @return Token
	 * @throws LoginFailedException
	 */
	public Token login() throws LoginFailedException {
		
		Token t = null;
		switch(config.authType) {
			case LDAP: {
				t = loginLDAP();
				break;
			}
			case APPROLE: {
				t = loginAppRole();
				break;
			}
			case USERPASS: {
				t = loginUserPass();
				break;
			}
			case TOKEN: {
				t = loginToken();
				break;
			}
			default: {
				// should fail before this
				break;
			}
		}
		
		return t;
	}
	
	public Token lookupSelf(Token t) throws ReadException {
		
		String url = config.authTokenLookupSelf();
		
		Toke toke = null;
		try {
			toke = client.get(url);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		if(toke.successful) {
			if(toke.response == null || toke.response.contains("errors")) {
				throw new ReadException("Errors on token lookup: "+toke.response);
			}else {
			   return new Token(t.getJson(),
					   t.fromSuccessfulLoginRequest,
					   new JSONObject(toke.response));
		    }
		}else {
			throw new ReadException("Failed to perform lookup on "+t);
		}
		
	}
	
}
