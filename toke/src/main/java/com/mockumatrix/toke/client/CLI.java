package com.mockumatrix.toke.client;

import org.json.JSONObject;

import com.mockumatrix.toke.Driver;
import com.mockumatrix.toke.DriverConfig;
import com.mockumatrix.toke.accessor.Toke;
import com.mockumatrix.toke.exception.LoginFailedException;
import com.mockumatrix.toke.exception.TokeException;

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
				.token("s.7QmL8ffLsV1m4r7aTQqQGT1H");
		
		Driver driver = new Driver(config);
		
		try {
		    driver.auth().login();
		}catch(LoginFailedException x) {
			x.printStackTrace();
			return;
		}
		
		Toke res = null;
		try {
			
			res = driver.kv().kvWrite("test/stuff", new JSONObject().put("key0", "value0").put("key1", 100));
			System.err.println(res.data().map());
			System.err.println(res.data().metadata());
			
			res = driver.kv().kvRead("test/stuff");
			System.err.println(res.data().map());
			System.err.println(res.data().metadata());
			
		} catch (TokeException e) {
			e.printStackTrace();
			return;
		}
		System.err.println(res);
	
		
	//	driver.auth().logoff();
		
	}

}
