package com.mockumatrix.toke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mockumatrix.toke.accessor.Toke;
import com.mockumatrix.toke.event.EventEnum;
import com.mockumatrix.toke.event.TokenEvent;
import com.mockumatrix.toke.event.TokenListener;
import com.mockumatrix.toke.exception.ReadException;

public class Sys implements TokenListener {

	protected DriverConfig config;
	protected Token token;
	protected Networking client;
	
	
	public Sys(DriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
	}
	
	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
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
