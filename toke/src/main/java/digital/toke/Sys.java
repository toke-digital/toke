/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.ReadException;
import digital.toke.policy.Policy;
import digital.toke.spec.AuthSpec;
import digital.toke.spec.SecretsEngineSpec;

/**
 * Implement the RESTful interface calls to the vault back-end
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class Sys extends ServiceBase implements TokenListener {

	private static final Logger logger = LogManager.getLogger(Sys.class);

	protected TokeDriverConfig config;
	protected Token token;
	protected Networking client;

	public Sys(TokeDriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		logger.info("Initialized Sys driver instance");
	}

	@Override
	public void tokenEvent(TokenEvent evt) {
		if (evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			countDown();
			logger.info("Token with accessor " + token.accessor() + " set on Sys");
		}

		// if(evt.getType().equals(EventEnum.SET_LATCH)) {
		// refreshLatch();
		// latch();
		// logger.info("Reloaded token on a Sys instance.");
		// }

		if (evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
			// countDown();
			logger.info("Reloaded token on a Sys instance.");
		}
	}
	
	
	
	//************  Policy Support ************//
	
	public Toke listPolicies() throws ReadException {
		String url = config.baseURL().append("/sys/policy").toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on /sys/policy");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke readPolicy(String policyName) throws ReadException {
		String url = config.baseURL().append("/sys/policy/"+policyName).toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on /sys/policy/<polName>");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke writePolicy(String policyName, String policyJSON) throws ReadException {
		String url = config.baseURL().append("/sys/policy/"+policyName).toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.put(url, policyJSON, true);
			// we expect a 204 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on /sys/policy/<polName>");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	/**
	 * The policy is an ACL instruction regarding an individual path. See https://www.vaultproject.io/docs/concepts/policies.html
	 * The API is basically one policy at a time - fairly limited support IMHO.
	 * 
	 * @param policyName
	 * @param policy
	 * @return
	 */
	public Toke createUpdatePolicy(String policyName, Policy policy) throws ConfigureException {
		String url = config.baseURL().append("/sys/policy/"+policyName).toString();
		logger.debug("Using: " + url);
		
		try {
			Toke response = client.put(url, policy.toString(), true);
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on /sys/policy/"+policyName);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	
	
	// ******************* END Policy Support ***************** //
	
	// *******************  Mount support (create/enable secrets engines ****************  //
	
	
	public Toke listMounts() throws ReadException {
		String url = config.baseURL().append("/sys/mounts").toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on /sys/mounts");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	/**
	 * Call the unfortunately named /sys/mounts.
	 * 
	 * Note: according to the documentation, "path" must be ASCII encoded.
	 * 
	 * @param name
	 * @param params
	 * @return
	 * @throws ConfigureException
	 */
	public Toke enableSecretsEngine(String path, SecretsEngineSpec params) throws ConfigureException {
		String url = config.baseURL().append("/sys/mounts/"+path).toString();
		logger.debug("Using: " + url);
		
		try {
			Toke response = client.put(url, params.toString(), true);
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on /sys/mounts/"+path);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	
	// TODO
	// disable
	
	// read mount configuration
	
	
	
	// tune mount configuration
	
	
	
	
	
	// *******************  END Mount support (create/enable secrets engines ****************  //
	
	// ******************** START SYS AUTH configure/enable methods ***************************** //
	
	public Toke listAuth() throws ReadException {
		String url = config.baseURL().append("/sys/auth").toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on /sys/auth");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	
	/**
	 * Enable an auth method.
	 * 
	 * Note: the documentation says sudo privilege is required on /sys/auth to perform this action
	 * 
	 * @param path
	 * @param spec
	 * @return
	 * @throws ConfigureException
	 */
	public Toke enableAuthMethod(String path, AuthSpec spec) throws ConfigureException {
		String url = config.baseURL().append("/sys/auth/"+path).toString();
		logger.debug("Using: " + url);
		
		try {
			Toke response = client.put(url, spec.toString(), true);
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on /sys/mounts/"+path);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	
	
	// ******************** END SYS AUTH configure/enable methods ***************************** //
	

	public Toke capabilities(String token, String path) throws ReadException {
		List<String> paths = new ArrayList<String>(1);
		paths.add(path);
		return capabilities(token, paths);
	}

	/**
	 * Implement a call to sys/capabilities
	 * 
	 * @param token
	 * @param paths
	 * @return
	 * @throws ReadException
	 */
	public Toke capabilities(String token, List<String> paths) throws ReadException {

		latch();

		String url = config.baseURL().append("/sys/capabilities").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("token", token).put("paths", new JSONArray(paths));

		logger.debug(json.toString(4));

		try {
			Toke response = client.post(url, json.toString());
			// we expect a 200 per the documentation
			readExceptionExcept(response, 200);
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}

	public Toke capabilitiesSelf(List<String> paths) throws ReadException {

		latch();

		String url = config.baseURL().append("/sys/capabilities-self").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject()
				// .put("token", token.clientToken())
				.put("paths", new JSONArray(paths));

		logger.debug(json.toString(4));

		try {
			Toke response = client.post(url, json.toString());
			// we expect a 200 per the documentation
			readExceptionExcept(response, 200);
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}

	// TODO, parameters and HEAD
	public Toke health() throws ReadException {

		String url = config.baseURL().append("/sys/health").toString();

		try {
			return client.get(url);
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke sealStatus() throws ReadException {

		String url = config.baseURL().append("/sys/seal-status").toString();

		try {
			return client.get(url);
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	/**
	 * Return the last response
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
			configureExceptionExcept(response, 200);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	/**
	 * Just call init with a minimal security threshold - can be used for testing purposes
	 * 
	 * @return
	 * @throws ConfigureException
	 */
	public Toke init() throws ConfigureException {

		String url = config.baseURL().append("/sys/init").toString();
		logger.debug("Using: " + url);
		JSONObject json = new JSONObject().put("secret_shares", 2).put("secret_threshhold", 2);

		logger.debug(json.toString(4));

		try {
			Toke response = client.put(url, json.toString(), false);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ConfigureException("Failed to init");
			
			return response;
			
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}

}
