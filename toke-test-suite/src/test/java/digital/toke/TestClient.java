/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.toke.accessor.Toke;
import digital.toke.exception.ReadException;

public class TestClient {
	
	static TokeDriver driver;
	
	@BeforeAll
	public static void init() {

		File keyFile = new File("./target/runtime/keyFile.txt");
		HousekeepingConfig hc = new HousekeepingConfig()
				.reachable(true)
				.pingHost(true)
				.init(true)  // special case, on success root token will be inserted into config.token as it is assumed we will need it
				.unseal(true)
				.unsealKeys(keyFile); // keys will be written here
		
		TokeDriverConfig config = new TokeDriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8201)
				.kvName("toke-kv1") 
				.kv2Name("toke-kv2")
				.authType("token")  // normally would need to set token(token) but with init == true init method will do this for us
				.housekeepingConfig(hc);
		 
		driver = new TokeDriver(config);
		
    }
	
	@Test
	public void test() {
	
			assertTrue(driver != null);
			driver.isReady();
			assertNotNull(driver.sys.token);
		
		
	}

}
