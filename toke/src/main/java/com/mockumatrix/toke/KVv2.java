package com.mockumatrix.toke;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.mockumatrix.toke.accessor.Toke;
import com.mockumatrix.toke.exception.ConfigureException;
import com.mockumatrix.toke.exception.ReadException;
import com.mockumatrix.toke.exception.WriteException;

import static com.mockumatrix.toke.DriverConfig.*;

public class KVv2 extends KV {

	private static final Logger logger = LogManager.getLogger(KVv2.class);
	
	public KVv2(DriverConfig config, Networking client) {
		super(config,client);
		logger.info("Initialized KVv2 driver instance");
	}
	
	/**
	 * Returns the most recent version on this path
	 * 
	 * @param path
	 * @return
	 * @throws ReadException
	 */
	public Toke read(String path) throws ReadException {
		return read(path, -1);
	}

	/**
	 * Read a given version of this path
	 * 
	 * @param path
	 * @param version
	 * @return
	 * @throws ReadException
	 */
	public Toke read(String path, int version) throws ReadException {
		String url = config.kv2Path(KVv2DATA, path);
		if(version != -1) {
			url+="?version="+version;
		}
		Toke response = null;
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
	 * Configure the max versions and if cas (Check and Set) is to be required on calls.
	 * 
	 * @param max_versions
	 * @param cas_required
	 * @return
	 * @throws ConfigureException
	 */
	public Toke kvConfigure(int max_versions, boolean cas_required) throws ConfigureException {
		String url = config.kv2Path(KVv2CONFIG,null);
		JSONObject json = new JSONObject();
		json.put("max_versions", max_versions);
		json.put("cas_required", cas_required);
		try {
			Toke response = client.post(url, json.toString());
			// we expect a 204 per the documentation
			if(response.code==404) throw new ConfigureException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new ConfigureException("Unexpected HTTP Response Code: "+response.code);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	/**
	 * Return the config for this secrets engine
	 * 
	 * @return
	 * @throws ReadException
	 */
	public Toke kvReadConfig() throws ReadException {
		String url = config.kv2Path(KVv2CONFIG, null);
		
		Toke response = null;
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
	 * Use cas - write only if there is no such key already
	 * 
	 * @param path
	 * @param data
	 * @return
	 * @throws WriteException
	 */
    public Toke kvWriteIfKeyDoesntExist(String path, Map<String,Object> data) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
		top.put("options", new JSONObject().put("cas", 0));
		return kvCreateUpdate(path, top.toString());
	}
    
    /**
     * Write regardless (no check and set) assuming the token has privileges to do so
     * 
     * @param path
     * @param data
     * @return
     * @throws WriteException
     */
    public Toke kvWrite(String path, Map<String,Object> data) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
	//	top.put("options", new JSONObject().put("cas", checkAndSet));
		return kvCreateUpdate(path, top.toString());
	}
	
    /**
     * Write only to the version indicated by the version number
     * 
     * @param path
     * @param data
     * @param version
     * @return
     * @throws WriteException
     */
	public Toke kvWriteVersion(String path, Map<String,Object> data, int version) throws WriteException {
		
		JSONObject top = new JSONObject().put("data", data);
		top.put("options", new JSONObject().put("cas", version));
		return kvCreateUpdate(path, top.toString());
	}
	
	/**
	 * <p>Create or update data along a path - this is the workhorse method, the 
	 * others are syntactical sugar and call this one.</p>
	 * 
	 * <p>https://www.vaultproject.io/api/secret/kv/kv-v2.html#create-update-secret</p>
	 * 
	 * @param path
	 * @param jsonData
	 * @return
	 * @throws WriteException
	 */
	public Toke kvCreateUpdate(String path, String jsonData) throws WriteException {
		String url = config.kv2Path(KVv2DATA,path);
		
		try {
			Toke response = client.post(url, jsonData);
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
			return response;
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}
	
	/**
	 * List keys in a path
	 * 
	 * @param path
	 * @return
	 * @throws ReadException
	 */
	public Toke kvList(String path) throws ReadException {
		String url = config.kv2Path(KVv2METADATA, path);
		
		Toke response = null;
		try {
			response = client.list(url);
			// we expect a 200 per the documentation
			if(response.code==404) throw new ReadException("Http 404 - this is usually a problem with the path.");
			if(response.code!=200) throw new ReadException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		return response;
	}
	
	/**
	 * Blow away the given versions permanently
	 * 
	 * @param path
	 * @param versions
	 * @return
	 * @throws WriteException
	 */
	public Toke kvDestroy(String path, int [] versions) throws WriteException {
		String url = config.kv2Path(KVv2DESTROY, path);
		
		JSONObject obj = new JSONObject().put("versions", versions);
		
		Toke response = null;
		try {
			response = client.post(url, obj.toString());
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new WriteException(e);
		}
		
		return response;
	}
	
	/**
	 * Soft-Delete the previous version to the current one
	 * 
	 * @param path
	 * @return
	 * @throws WriteException
	 */
	public Toke kvDelete(String path) throws WriteException {
		String url = config.kv2Path(KVv2DATA, path);
		
		Toke response = null;
		try {
			response = client.delete(url);
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new WriteException(e);
		}
		
		return response;
	}
	
	/**
	 * Soft-Delete some set of versions 
	 * 
	 * @param path
	 * @param versionsToDelete
	 * @return
	 * @throws WriteException
	 */
	public Toke kvDelete(String path, int [] versionsToDelete) throws WriteException {
		String url = config.kv2Path(KVv2DELETE, path);
		
		JSONObject obj = new JSONObject().put("versions", versionsToDelete);
		
		Toke response = null;
		try {
			response = client.post(url, obj.toString());
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new WriteException(e);
		}
		
		return response;
	}
	
	/**
	 * Restore previously deleted versions
	 * 
	 * @param path
	 * @param versionsToUndelete
	 * @return
	 * @throws WriteException
	 */
	public Toke kvUndelete(String path, int [] versionsToUndelete) throws WriteException {
		String url = config.kv2Path(KVv2UNDELETE, path);
		
		JSONObject obj = new JSONObject().put("versions", versionsToUndelete);
		
		Toke response = null;
		try {
			response = client.post(url, obj.toString());
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
		} catch (IOException e) {
			throw new WriteException(e);
		}
		
		return response;
	}

}
