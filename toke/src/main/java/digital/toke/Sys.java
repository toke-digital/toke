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

/**
 * Implement the RESTful interface calls to the vault back-end
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Sys extends ServiceBase implements TokenListener {

	private static final Logger logger = LogManager.getLogger(Sys.class);

	protected DriverConfig config;
	protected Token token;
	protected Networking client;

	public Sys(DriverConfig config, Networking client) {
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

		latch();

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

}
