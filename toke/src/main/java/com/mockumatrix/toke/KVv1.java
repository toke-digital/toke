package com.mockumatrix.toke;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.mockumatrix.toke.accessor.Toke;
import com.mockumatrix.toke.exception.ReadException;
import com.mockumatrix.toke.exception.WriteException;

public class KVv1 extends KV {

	public KVv1(DriverConfig config, Networking client) {
		super(config,client);
		
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
		String url = config.kv1Path(path);
		
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
		String url = config.kv1Path(path);
		
		try {
			Toke response = client.post(url, jsonData);
			// we expect a 200 per the documentation
			if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
			if(response.code!=204) throw new WriteException("Unexpected HTTP Response Code: "+response.code);
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
		String url = config.kv1Path(path);
		
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
	 * Delete the previous version to the current one
	 * 
	 * @param path
	 * @return
	 * @throws WriteException
	 */
	public Toke kvDelete(String path) throws WriteException {
		String url = config.kv1Path(path);
		
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
	
}
