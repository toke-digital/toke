/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */

package digital.toke;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.toke.TokeDriver;
import digital.toke.TokeDriverConfig;
import digital.toke.accessor.Toke;
import digital.toke.exception.TokeException;

/**
 * The testing requires a fairly specific vault instance to be set up and that's not documented quite yet.
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class DriverTest {
	
	static TokeDriver driver;
	
	@BeforeAll
	public static void load() {
		
		TokeDriverConfig config = new TokeDriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.kvName("toke-kv1") // driver will provide coverage for one kv1 and one kv2
				.kv2Name("toke-kv2")
				.authType("USERPASS")
				.username("bob")
				.password("password1");
		
		// driver will auto-login and block on rest-calls until it is ready
		driver = new TokeDriver(config);
		
	}

	@Test
	public void testKVv1() {
		
		Toke res = null;
		try {
			res = driver.kv().kvWrite("test/stuff", new JSONObject().put("key0", "value0").put("key1", 100));

			Assertions.assertEquals(204, res.code);// successful write
			
			Toke tr = driver.kv().kvRead("test/stuff");
	
			Assertions.assertTrue(tr.data().map().containsKey("key0"));
			Assertions.assertTrue(tr.data().map().containsKey("key1"));
			
			tr = driver.kv().kvList("test/");
			
			// support for forgetful typists
			tr = driver.kv().kvList("test");
			tr.kvList().secrets();
			
			tr = driver.kv().kvDelete("test/stuff");
		
			
		} catch (TokeException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}
	
	@Test
	public void testSys() {
		
		Toke res = null;
		try {
			res = driver.kv().kvWrite("test/stuff2", new JSONObject().put("key0", "value0").put("key1", 100));
			
			List<String> l = new ArrayList<String>();
			l.add("text/stuff2");
			res = driver.sys().capabilitiesSelf(l);
			
			Assertions.assertNotNull(res);
			System.err.println(res);
			
			res = driver.sys().health();
			Assertions.assertNotNull(res.toString());
			
		} catch (TokeException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}
	

}
