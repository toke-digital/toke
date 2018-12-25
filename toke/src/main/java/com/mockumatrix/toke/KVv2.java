package com.mockumatrix.toke;

import java.io.IOException;

public class KVv2 extends KV {

	public KVv2(DriverConfig config, Networking client) {
		super(config,client);
		
	}

	public APIResponse read(String path) {
		String url = config.kv2Path(DriverConfig.KVv2DATA, path);
		System.err.println(url);
		try {
			return client.get(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

}
