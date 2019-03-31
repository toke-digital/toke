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
import digital.toke.auth.UserPass;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.LoginFailedException;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;

/**
 * <p>The auth module implements a vault login using various auth types such as
 * LDAP and APPROLE. Tokens are sent wrapped in events to interested parties and
 * managed by the TokenManager instance</p>
 * 
 * <p>See https://www.vaultproject.io/docs/auth/</p>
 * 
 * @author Dave
 *
 */
public class Auth {

	private static final Logger logger = LogManager.getLogger(Auth.class);

	TokeDriverConfig config;
	Networking client;
	
	UserPass userpass;

	public Auth(TokeDriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		logger.info("Auth instance " + this.hashCode() + " configured");
		
		userpass = new UserPass(config,client);
		
	}

	public void logoff(Token token) {
		// destroy token TODO
	}

	public boolean pingHost() {
		return client.pingHost(config.host, config.port, 200);
	}

	public boolean hostIsReachable() {
		return client.checkIsReachable(config.host);
	}

	// requires permission on auth/token/renew-self
	public Token renewSelf(Token token) throws WriteException, ReadException {
		String url = config.authTokenRenewSelf();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("increment", "1h"); // TODO make configurable
		logger.debug(json.toString(4));

		Toke toke = null;
		try {
			toke = client.post(url, json.toString());
		} catch (IOException e) {
			throw new WriteException(e);
		}

		if (!toke.successful)
			throw new WriteException("Failed to renew token with accessor " + token.accessor());

		Token newToken = new Token(token.loginConfig, new JSONObject(toke.response), toke.successful);
		return lookupSelf(newToken);
	}

	// requires permission on auth/token/renew
	public Token renewPeriodic(Token token) throws WriteException, ReadException {
		String url = config.authTokenRenew();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("token", token.clientToken());
		logger.debug(json.toString(4));

		Toke toke = null;
		try {
			toke = client.post(url, json.toString());
		} catch (IOException e) {
			throw new WriteException(e);
		}

		if (!toke.successful)
			throw new WriteException("Failed to renew token with accessor " + token.accessor());

		Token newToken = new Token(token.loginConfig, new JSONObject(toke.response), toke.successful);
		return lookupSelf(newToken);

	}

	public Toke checkSealStatus() throws ReadException {

		String url = config.baseURL().append("/sys/seal-status").toString();

		try {
			return client.get(url, false);
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
		int count = 1;
		for (String key : keys) {
			logger.debug("sending unseal key " + count);
			toke = unseal(key, reset, migrate);
			count++;
		}
		return toke;
	}

	public Toke unseal(String key, boolean reset, boolean migrate) throws ConfigureException {

		String url = config.baseURL().append("/sys/unseal").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("key", key).put("reset", reset).put("migrate", migrate);

		// logger.debug(json.toString(4));

		try {
			Toke response = client.put(url, json.toString(), false);
			// we expect a 200 per the documentation
			if (response.code != 200)
				throw new ConfigureException(response.toString());
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}

	// Logins. All logins are POSTs

	Token loginLDAP() throws LoginFailedException {
		String url = config.authLdapLogin();
		JSONObject json = new JSONObject();
		json.put("password", config.loginConfig.password);
		return httpLogin(config.loginConfig, url, json);
	}
	
	Token loginLDAP(LoginConfig loginConfig) throws LoginFailedException {
		String url = config.authLdapLogin();
		JSONObject json = new JSONObject();
		json.put("password", loginConfig.password);
		return httpLogin(loginConfig, url, json);
	}

	/**
	 * Assumption: bind_secret_id is true for this app role, which forces the
	 * secret_id to be used
	 * 
	 * @throws LoginFailedException
	 */
	Token loginAppRole() throws LoginFailedException {
		String url = config.authAppRoleLogin();
		JSONObject json = new JSONObject();
		json.put("role_id", config.loginConfig.roleId);
		json.put("secret_id", config.loginConfig.secretId);
		return httpLogin(config.loginConfig, url, json);
	}
	
	Token loginAppRole(LoginConfig loginConfig) throws LoginFailedException {
		String url = config.authAppRoleLogin();
		JSONObject json = new JSONObject();
		json.put("role_id", loginConfig.roleId);
		json.put("secret_id", loginConfig.secretId);
		return httpLogin(loginConfig, url, json);
	}

	Token loginUserPass() throws LoginFailedException {
		logger.debug("in loginUserPass");
		String url = config.authUserPassLogin();
		JSONObject json = new JSONObject();
		json.put("password", config.loginConfig.password);
		return httpLogin(config.loginConfig, url, json);
	}
	
	Token loginUserPass(LoginConfig loginConfig) throws LoginFailedException {
		logger.debug("in loginUserPass");
		String url = config.authUserPassLogin();
		JSONObject json = new JSONObject();
		json.put("password", loginConfig.password);
		return httpLogin(loginConfig, url, json);
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

		return new Token(config.loginConfig, new JSONObject(result.response), result.successful);

	}
	
	Token loginToken(LoginConfig loginConfig) throws LoginFailedException {
		String url = config.authTokenLogin();
		JSONObject json = new JSONObject();
		// TODO at the moment only supporting one config property here
		json.put("renewable", config.renewable);
		Toke result = null;
		try {
			result = client.loginToken(url, json.toString(), loginConfig.token);
		} catch (IOException e) {
			throw new LoginFailedException(e);
		}

		return new Token(loginConfig, new JSONObject(result.response), result.successful);

	}
	

	private Token httpLogin(LoginConfig lc, String url, JSONObject json) throws LoginFailedException {

		logger.debug("in httpLogin for "+lc.toString());
		Toke result = null;
		try {
			result = client.login(url, json.toString());
			logger.debug("got result: " + result);
		} catch (IOException e) {
			throw new LoginFailedException(e);
		}
		return new Token(lc, new JSONObject(result.response), result.successful);
	}

	/**
	 * This is used internally with asynchronous (auto-login) situations as with a vault init
	 * 
	 * @return Token
	 * @throws LoginFailedException
	 */
	Token login() throws LoginFailedException {

		Token t = null;
		switch (config.loginConfig.authType) {
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
	
	/**
	 * This is a synchronous login for use when we want to add additional managed logins. It returns the token handle (key)
	 * for the Token as found in the TokenManager's HashMap
	 * 
	 * @param lc
	 * @return
	 * @throws LoginFailedException
	 */
	public String login(LoginConfig lc) throws LoginFailedException {

		Token t = null;
		switch (lc.authType) {
			case LDAP: {
				t = loginLDAP(lc);
				break;
			}
			case APPROLE: {
				t = loginAppRole(lc);
				break;
			}
			case USERPASS: {
				t = loginUserPass(lc);
				break;
			}
			case TOKEN: {
				t = loginToken(lc);
				break;
			}
			default: {
				// should fail before this
				break;
			}
		}

		return t.tokenHandle;
	}
	

	/**
	 * Enrich the token with current lookup data. The original Token instance is unchanged; a new instance is returned which is cloned + the lookup data
	 * 
	 * @param t
	 * @return
	 * @throws ReadException
	 */
	public Token lookupSelf(Token t) throws ReadException {

		String url = config.authTokenLookupSelf();
		logger.debug("using url = " + url);

		Toke toke = null;
		try {
			toke = client.get(url);
		} catch (IOException e) {
			throw new ReadException(e);
		}

		if (toke.successful) {
			if (toke.response == null || toke.response.contains("errors")) {
				throw new ReadException("Errors on token lookup: " + toke.response);
			} else {
				return new Token(t.loginConfig, t.getJson(), t.fromSuccessfulLoginRequest, new JSONObject(toke.response));
			}
		} else {
			throw new ReadException("Failed to perform lookup: " + toke.toString());
		}

	}

	public Token lookup(Token t) throws ReadException {

		String url = config.authTokenLookup();
		logger.debug("using url = " + url);

		JSONObject json = new JSONObject();
		json.put("token", t.clientToken());

		Toke toke = null;
		try {
			toke = client.post(url, json.toString());
		} catch (IOException e) {
			throw new ReadException(e);
		}

		if (toke.successful) {
			if (toke.response == null || toke.response.contains("errors")) {
				throw new ReadException("Errors on token lookup: " + toke.response);
			} else {
				return new Token(t.loginConfig, t.getJson(), t.fromSuccessfulLoginRequest, new JSONObject(toke.response));
			}
		} else {
			throw new ReadException("Failed to perform lookup: " + toke.toString());
		}

	}
	
	
	public Toke initVault() throws ConfigureException {
		return initVault(3,2);
	}

	/**
	 * Just call init with a minimal security threshold - can be used for testing
	 * purposes
	 * 
	 * @return
	 * @throws ConfigureException
	 */
	public Toke initVault(int secretShares, int secretThreshhold) throws ConfigureException {

		String url = config.baseURL().append("/sys/init").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("secret_shares", secretShares).put("secret_threshold", secretThreshhold);

		logger.debug(json.toString(4));

		try {
			Toke response = client.put(url, json.toString(), false);
			// we expect a 200 per the documentation
			if (response.code != 200) {
				throw new ConfigureException("Failed to init: "+response.response);
				
			}
			return response;

		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}

	public UserPass userPass() {
		return userpass;
	}
	
	
	
}
