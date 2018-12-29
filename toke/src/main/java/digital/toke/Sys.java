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
import digital.toke.exception.ReadException;
/**
 * Implement the RESTful interface calls to the vault back-end
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Sys implements TokenListener {

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
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			logger.info("Token with accessor "+token.accessor()+" set on Sys");
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
		String url = config.baseURL().append("/sys/capabilities").toString();
		JSONObject json = new JSONObject()
				.put("token", token)
				.put("paths", new JSONArray(paths));
		
		System.err.println(json.toString(4));
		
		try {
			Toke response = client.post(url, json.toString());
			// we expect a 200 per the documentation
			if(response.code==404) throw new ReadException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new ReadException("Unexpected HTTP Response Code: "+response.code + " "+response.response);
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke capabilitiesSelf(String path) throws ReadException {
		List<String> paths = new ArrayList<String>(1);
		paths.add(path);
		return capabilities(this.token.clientToken(), paths);
	}
	
	public Toke health() throws ReadException {
		
		String url = config.baseURL().append("/sys/health").toString();
		
		try {
			return client.get(url);
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	

}
