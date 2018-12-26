package com.mockumatrix.toke;

import java.io.IOException;
import java.util.Map;

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
	
	/**
	 * Use cas - write only if there is no such key
	 * 
	 * @param path
	 * @param data
	 * @return
	 * @throws WriteException
	 */
    public APIResponse kvWriteIfKeyDoesntExist(String path, Map<String,Object> data) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
		top.put("options", new JSONObject().put("cas", 0));
		return kvCreateUpdate(path, top.toString());
	}
    
    /**
     * Write regardless (no check and set)
     * @param path
     * @param data
     * @return
     * @throws WriteException
     */
    public APIResponse kvWrite(String path, Map<String,Object> data) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
	//	top.put("options", new JSONObject().put("cas", checkAndSet));
		return kvCreateUpdate(path, top.toString());
	}
	
    /**
     * Write with CAS - write only the version indicated by the version number (the cas value)
     * 
     * @param path
     * @param data
     * @param checkAndSet
     * @return
     * @throws WriteException
     */
	public APIResponse kvCreateUpdate(String path, Map<String,Object> data, int checkAndSet) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
		top.put("options", new JSONObject().put("cas", checkAndSet));
		return kvCreateUpdate(path, top.toString());
	}
	
	public APIResponse kvCreateUpdate(String path, String jsonData) throws WriteException {
		String url = config.kv2Path(KVv2DATA,path);
		
		try {
			APIResponse response = client.post(url, jsonData);
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
			return response;
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}

}
