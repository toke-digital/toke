package com.mockumatrix.toke;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mockumatrix.toke.accessor.Toke;
import com.mockumatrix.toke.exception.LoginFailedException;
import com.mockumatrix.toke.exception.TokeException;

import org.junit.Assert;

public class DriverTest {
	
	static Driver driver;
	
	@BeforeAll
	public static void load() {
		
		String token = "";
		
		try {
			token = new String(Files.readAllBytes(new File("C:\\token.txt").toPath()), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.authType("TOKEN")
				.token(token);
		
		driver = new Driver(config);
		
		try {
		    driver.auth().login();
		}catch(LoginFailedException x) {
			x.printStackTrace();
			return;
		}
	}

	@Test
	public void testKVv1() {
		
		Toke res = null;
		try {
			res = driver.kv().kvWrite("test/stuff", new JSONObject().put("key0", "value0").put("key1", 100));

			Assert.assertEquals(204, res.code);// successful write
			
			Toke tr = driver.kv().kvRead("test/stuff");
			
			Assert.assertTrue(tr.data().map().containsKey("key0"));
			Assert.assertTrue(tr.data().map().containsKey("key1"));
			
			tr = driver.kv().kvList("test/");
			Assert.assertEquals(1, tr.kvList().secrets().size());
			
			// support for forgetful typists
			tr = driver.kv().kvList("test");
			Assert.assertEquals(1, tr.kvList().secrets().size());
			
			tr = driver.kv().kvDelete("test/stuff");
			Assert.assertEquals(true, tr.successful);
		
			
		} catch (TokeException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Test
	public void testSys() {
		
		Toke res = null;
		try {
			res = driver.kv().kvWrite("test/stuff2", new JSONObject().put("key0", "value0").put("key1", 100));
			res = driver.sys().capabilitiesSelf("test/stuff2");
			
			Assert.assertNotNull(res);
			System.err.println(res);
			
		
			
		} catch (TokeException e) {
			e.printStackTrace();
			return;
		}
	}
	

}
