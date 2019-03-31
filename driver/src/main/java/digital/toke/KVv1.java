/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import digital.toke.accessor.DataResponseDecorator;
import digital.toke.accessor.Toke;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;
import okhttp3.HttpUrl;

/**
 * Implement the RESTful interface calls to KVv1 secrets engine
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class KVv1 extends KV {
	
	private static final Logger logger = LogManager.getLogger(KVv1.class);

	public KVv1(TokeDriverConfig config, Networking client) {
		super(config,client);
		logger.info("Initialized KVv1 driver instance");
	}
	

	/**
	 * Read a set of key/value pairs written on this path
	 * 
	 * @param path
	 * @param version
	 * @return
	 * @throws ReadException
	 */
	public Toke kvRead(String path) throws ReadException {
		
		latch();
		
		String url = config.kv1Path(path);
		
		Toke response = null;
		try {
			response = client.get(url);
			// we expect a 200 per the documentation
			readExceptionExcept(response, 200);
		} catch (IOException e) {
			throw new ReadException(e);
		}
		
		return response;
	}
	
	/**
	 * Create or over-write a path and set key/value pairs from the supplied JSONObject
	 * @param path
	 * @param obj
	 * @return
	 * @throws WriteException
	 */
	public Toke kvWrite(String path, JSONObject obj) throws WriteException {
		return kvCreateUpdate(path, obj.toString());
	}
	
	/**
	 * Create or over-write a path and set key/value pairs from the supplied map
	 * @param path
	 * @param map
	 * @return
	 * @throws WriteException
	 */
	public Toke kvWrite(String path, Map<String,Object> map) throws WriteException {
		JSONObject obj = new JSONObject(map);
		return kvCreateUpdate(path, obj.toString());
	}
	
	/**
	 * Just write one key and one value, note, this overwrites what is there, 
	 * use accumulate to add
	 * 
	 * @param path
	 * @param key
	 * @param value
	 * @return
	 * @throws WriteException
	 */
	public Toke write(String path, String key, String value) throws WriteException {
		JSONObject obj = new JSONObject();
		return kvCreateUpdate(path, obj.accumulate(key, value).toString());
	}
	
	/**
	 * Like an append function. We read and add the additional value on the end. If
	 * that key is already defined, it will be overwritten (updated). 
	 * @param path
	 * @param key
	 * @param value
	 * @return
	 * @throws WriteException
	 */
	public Toke accumulate(String path, String key, String value) throws WriteException {
		
		Map<String,Object> current = null;
		
		try {
		
			Toke t = this.kvRead(path);
			DataResponseDecorator d = new DataResponseDecorator(t);
			current = d.map();
		
		}catch(ReadException x) {
			// may mean no secret exists
		}
		if(current == null) {
			current = new HashMap<String,Object>();
			current.put(key,value);
		   return kvWrite(path, current);
		}else {
			current.put(key,value);
			return kvWrite(path,current);
		}
	}
	
	
	/**
	 * <p>Create or update by overwriting a path with keys and values in a json-encoded map
	 * such as:</p>
	 * 
	 * <pre>
	 * {
	 *   "key0": "value",
	 *   "key1: "value1"
	 * }
	 * </pre>
	 * 
	 * @param path
	 * @param jsonData
	 * @return
	 * @throws WriteException
	 */
	private Toke kvCreateUpdate(String path, String jsonData) throws WriteException {
		
		latch();
		
		String url = config.kv1Path(path);
		logger.debug("Using: "+url);
		try {
			Toke response = client.post(url, jsonData);
			// we expect a 200 per the documentation
			writeExceptionExcept(response, 204);
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
		
		latch();
		
		HttpUrl url = config.kv1List(path);
		
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
	
	/**
	 * Return a list of the keys found, or an empty list if there was an error or nothing on that path. 
	 * 
	 * @param path
	 * @return
	 */
    public List<String> list(String path) {
		
    	List<String> keys = new ArrayList<String>();
    	Toke t = null;
    	try {
			t = this.kvList(path);
		} catch (ReadException e) {
			return keys; // none
		}
    	
    	if(t != null && t.successful) {
    		 t.accessor().json().getJSONObject("data").getJSONArray("keys").toList();
    	}
    	
    	return null;
	}


	
	/**
	 * Delete the previous version to the current one
	 * 
	 * @param path
	 * @return
	 * @throws WriteException
	 */
	public Toke kvDelete(String path) throws WriteException {
		
		latch();
		
		String url = config.kv1Path(path);
		
		Toke response = null;
		try {
			response = client.delete(url);
			// we expect a 200 per the documentation
			writeExceptionExcept(response, 204);
		} catch (IOException e) {
			throw new WriteException(e);
		}
		
		return response;
	}
	
}
