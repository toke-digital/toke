/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point. You can instantiate multiple instances of this class if required. 
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class TokeDriver {

	private static final Logger logger = LogManager.getLogger(TokeDriver.class);
	
	final TokeDriverConfig config;
	final Networking httpClient;
	final TokenManager tokenManager;
	
	final Auth auth;
	
	final Sys sys;
	
	final KVv1 kvv1;
	final KVv2 kvv2;
	

	public TokeDriver(TokeDriverConfig config) {
		super();
		this.config = config;
		httpClient = new Networking();
		
		auth = new Auth(config, httpClient);
		
		tokenManager = new TokenManager(config, auth);
		tokenManager.addTokenListener(httpClient);
		
		sys = new Sys(config, httpClient);
		tokenManager.addTokenListener(sys);
		
		kvv1 = new KVv1(config, httpClient);
		tokenManager.addTokenListener(kvv1);
		
		kvv2 = new KVv2(config, httpClient);
		tokenManager.addTokenListener(kvv2);
		
		logger.info("Driver instance "+this.getClass().hashCode() +" initialized");
		
	}
	
	/**
	 * Block until Driver is ready for use, i.e., after vault is unsealed and we have a token
	 * 
	 * @return
	 */
	public boolean isReady() {
		
		DefaultHousekeepingImpl impl = new DefaultHousekeepingImpl(this.tokenManager);
		impl.run(); // initial

		tokenManager.initScheduler(impl);
		return true;
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
	
	@Override
	public String toString() {
		return super.toString();
	}

}
