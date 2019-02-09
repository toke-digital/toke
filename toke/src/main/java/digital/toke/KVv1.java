/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

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

	public KVv1(DriverConfig config, Networking client) {
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
	 * Create or over-write a path and set key/value pairs
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
