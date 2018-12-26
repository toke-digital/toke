package com.mockumatrix.toke.client;

import com.mockumatrix.toke.APIResponse;
import com.mockumatrix.toke.Driver;
import com.mockumatrix.toke.DriverConfig;
import com.mockumatrix.toke.exception.LoginFailedException;
import com.mockumatrix.toke.exception.ReadException;

/**
 * Useful CLI and demo client
 * 
 * @author Dave
 *
 */
public class CLI {

	public CLI() {}

	public static void main(String[] args) {
		new CLI().run(args);
	}
	
	private void run(String [] args) {
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.authType("TOKEN")
				.token("s.59eS6J6SpXD230kCSt0KbQya");
		
		Driver driver = new Driver(config);
		
		try {
		    driver.auth().login();
		}catch(LoginFailedException x) {
			x.printStackTrace();
			return;
		}
		
		APIResponse res = null;
		try {
			res = driver.kv2().read("test/mysecret");
		} catch (ReadException e) {
			e.printStackTrace();
			return;
		}
		System.err.println(res);
		System.err.println(res.data());
		System.err.println(res.metadata());
		
		
	}

}
