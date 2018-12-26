package com.mockumatrix.toke;

import java.io.IOException;

import org.json.JSONObject;

import com.mockumatrix.toke.exception.ConfigureException;
import com.mockumatrix.toke.exception.ReadException;
import com.mockumatrix.toke.exception.WriteException;

import static com.mockumatrix.toke.DriverConfig.*;

public class KVv2 extends KV {

	public KVv2(DriverConfig config, Networking client) {
		super(config,client);
		
	}
	
	/**
	 * Returns the most recent version
	 * 
	 * @param path
	 * @return
	 * @throws ReadException
	 */
	public APIResponse read(String path) throws ReadException {
		return read(path, -1);
	}

	public APIResponse read(String path, int version) throws ReadException {
		String url = config.kv2Path(KVv2DATA, path);
		if(version != -1) {
			url+="?version="+version;
		}
		APIResponse response = null;
		try {
			response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code==404) throw new ReadException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new ReadException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		return response;
	}
	
	public APIResponse kvConfigure(int max_versions, boolean cas_required) throws ConfigureException {
		String url = config.kv2Path(KVv2CONFIG,null);
		JSONObject json = new JSONObject();
		json.put("max_versions", max_versions);
		json.put("cas_required", cas_required);
		try {
			APIResponse response = client.post(url, json.toString());
			// we expect a 204 per the documentation
			if(response.code==404) throw new ConfigureException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new ConfigureException("Unexpected HTTP Response Code: "+response.code);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	public APIResponse kvReadConfig() throws ReadException {
		String url = config.kv2Path(KVv2CONFIG, null);
		
		APIResponse response = null;
		try {
			response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code==404) throw new ReadException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new ReadException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		return response;
	}
	
	public APIResponse kvCreateUpdate(String path, JSONObject data) throws WriteException {
		String url = config.kv2Path(KVv2DATA,path);
		
		try {
			APIResponse response = client.post(url, data.toString());
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
			return response;
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}

}
