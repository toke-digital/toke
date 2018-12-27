package com.mockumatrix.toke;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mockumatrix.toke.exception.LoginFailedException;
import com.mockumatrix.toke.exception.TokeException;
import com.mockumatrix.toke.response.APIResponse;
import com.mockumatrix.toke.response.APIResponseBase;

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
		
		APIResponse res = null;
		try {
			res = driver.kv().kvWrite("test/stuff", new JSONObject().put("key0", "value0").put("key1", 100));
			res = driver.kv().kvRead("test/stuff");
			Assert.assertNotNull(res);
			Assert.assertEquals("value0", res.data().get("key0"));
			Assert.assertEquals(100, res.data().get("key1"));
			
			Assert.assertEquals(1, driver.kv().kvList("test/").keys().size());
			// support for forgetful typists
			Assert.assertEquals(1, driver.kv().kvList("test").keys().size());
			
			APIResponseBase resBase = (APIResponseBase) driver.kv().kvDelete("test/stuff");
			Assert.assertEquals(204, resBase.code);
			
		} catch (TokeException e) {
			e.printStackTrace();
			return;
		}
	}

}