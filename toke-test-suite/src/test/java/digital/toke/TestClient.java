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


public class TestClient {
	
	static TokeDriver driver;
	
	@BeforeAll
	public static void init() {

		File keyFile = new File("./target/runtime/keyFile.json");
		HousekeepingConfig hc = new HousekeepingConfig()
				.reachable(true)
				.pingHost(true)
				.init(true)  // special case, on success root token will be inserted into config.token as it is assumed we will need it
				.unseal(true)
				.unsealKeys(keyFile) // keys and root token will be written here
				.build();
		
		System.out.println("housekeepingConfig: "+hc.toString());
		
		TokeDriverConfig config = new TokeDriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8201)
				.kvName("toke-kv1") 
				.kv2Name("toke-kv2")
				.authType("TOKEN")  // see AuthType enum for the suported types 
				.housekeepingConfig(hc)
				.build();
		
		System.out.println("housekeepingConfig: "+config.toString());
		 
		driver = new TokeDriver(config);
		
    }
	
	@Test
	public void test() {
	
			assertTrue(driver != null);
			driver.isReady();
			assertNotNull(driver.sys.token);
			
			
	}

}
