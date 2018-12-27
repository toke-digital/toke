package com.mockumatrix.toke;

public class Driver {

	final DriverConfig config;
	final Networking httpClient;
	final TokenManager tokenManager;
	
	final Auth auth;
	
	final Sys sys;
	
	final KVv1 kvv1;
	final KVv2 kvv2;
	

	public Driver(DriverConfig config) {
		super();
		this.config = config;
		httpClient = new Networking();
		
		auth = new Auth(config, httpClient);
		auth.addTokenListener(httpClient);
		
		tokenManager = new TokenManager();
		auth.addTokenListener(tokenManager);
		
		sys = new Sys(config,httpClient);
		auth.addTokenListener(sys);
		
		kvv1 = new KVv1(config,httpClient);
		auth.addTokenListener(kvv1);
		
		kvv2 = new KVv2(config,httpClient);
		auth.addTokenListener(kvv2);
		
	}
	
	public Auth auth() {
		return auth;
	}
	
	public Sys sys() {
		return sys;
	}
	
	public KVv1 kv() {
		return kvv1;
	}
	
	public KVv2 kv2() {
		return kvv2;
	}

}
