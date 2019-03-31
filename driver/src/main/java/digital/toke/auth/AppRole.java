/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.auth;

import java.io.IOException;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import digital.toke.Networking;
import digital.toke.ServiceBase;
import digital.toke.TokeDriverConfig;
import digital.toke.Token;
import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.ReadException;
import okhttp3.HttpUrl;

/**
 * "approle" API methods
 * 
 * @author daves
 *
 */
public class AppRole extends ServiceBase implements TokenListener {
	
	private static final Logger logger = LogManager.getLogger(AppRole.class);

	protected TokeDriverConfig config;
	protected Token token;
	protected Networking client;

	public AppRole(TokeDriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		logger.info("Initialized AppRole instance");
	}

	@Override
	public void tokenEvent(TokenEvent evt) {
		if (evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			countDown();
			logger.info("Token with accessor " + token.accessor() + " set on AppRole");
		}

		if (evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
			// countDown();
			logger.info("Reloaded token on an AppRole instance.");
		}
	}
	
	public Toke listRoles() throws ReadException {
		return listRoles("approle");
	}
	
	public Toke listRoles(String authPathSegment) throws ReadException {
		HttpUrl url = config.approleList("/auth/"+authPathSegment+"/role");
		logger.debug("Using: " + url);
		Toke response = null;
		try {
			response = client.list(url);
			// we expect a 200 per the documentation
			readExceptionExcept(response, 200);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		return response;
	}
	
	
	public Toke createUpdateAppRole(AppRoleSpec approle) throws ConfigureException {
		String url = config.baseURL().append("/auth/"+approle.authPath+"/role/"+approle.roleName).toString();
		logger.debug("Using: " + url);
		
		try {
			
			Toke response = client.post(url, approle.toString());
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on "+"/auth/"+approle.authPath+"/role/"+approle.roleName);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	public Toke deleteAppRole(String roleName, String authPathSegment) throws ConfigureException {
		StringBuilder b = new StringBuilder(); 
		b.append("/auth/").append(authPathSegment).append("/role/").append(roleName);
		
		String url = config.baseURL().append(b.toString()).toString();
		logger.debug("Using: " + url);
		
		try {
			Toke response = client.delete(url);
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on "+b.toString());
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	/**
	 * Normal case
	 * 
	 * @param username
	 * @return
	 * @throws ReadException
	 */
	public Toke readAppRole(String roleName) throws ReadException {
		return readAppRole(roleName,"approle");
	}
	
	/**
	 * AppRole was enabled on a custom path, such as "my-approle"
	 * 
	 * @param username
	 * @param authPath
	 * @return
	 * @throws ReadException
	 */
	public Toke readAppRole(String roleName, String authPath) throws ReadException {
		String url = config.baseURL().append("/auth/"+authPath+"/role/"+roleName).toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on "+"/auth/"+authPath+"/role/"+roleName);
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke readAppRoleRoleId(String roleName) throws ReadException {
		return readAppRoleRoleId(roleName,"approle");
	}
	
	public Toke readAppRoleRoleId(String roleName, String authPath) throws ReadException {
		String url = config.baseURL().append("/auth/"+authPath+"/role/"+roleName+"/role-id").toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on "+"/auth/"+authPath+"/role/"+roleName+"/role-id");
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}
	
	public Toke generateNewSecretId(String roleName, String authPathSegment, String jsonMetadata) throws ConfigureException {
		String url = config.baseURL().append("/auth/"+authPathSegment+"/role/"+roleName+"/secret-id").toString();
		logger.debug("Using: " + url);
		
		try {
			
			Toke response = null;
			
			if(jsonMetadata == null) {
				// API is obscure about this, they ask for a post but do not describe the case where no metadata is desired
				 JSONObject json = new JSONObject().put("metadata", new JSONObject()); // empty
				  response = client.post(url, json.toString());
			}else {
			  byte [] encoded = Base64.getEncoder().encode(jsonMetadata.getBytes());
			  String metadataBase64 = new String(encoded, "US-ASCII");
			  JSONObject json = new JSONObject().put("metadata", metadataBase64);
			  response = client.post(url, json.toString());
			}
			
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ConfigureException("Failed to get a 200 response on "+"/auth/"+authPathSegment+"/role/"+roleName+"/secret-id");
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}

}
