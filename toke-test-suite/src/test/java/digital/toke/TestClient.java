/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.toke.accessor.PolicyResponseDecorator;
import digital.toke.accessor.Toke;
import digital.toke.accessor.UserDataResponseDecorator;
import digital.toke.auth.UserPass;
import digital.toke.auth.UserSpec;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.LoginFailedException;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;
import digital.toke.spec.AuthSpec;
import digital.toke.spec.SecretsEngineSpec;
import digital.toke.spec.SecretsEngineType;

/**
 * NOTE: this is expected to be run in the context of a full Maven build during
 * the test phase, so the vault instance test-bed is set up.
 * 
 * @author daves
 *
 */
public class TestClient {

	static TokeDriver driver;

	/**
	 * This method creates a vault instance and populates it for testing on each run
	 * 
	 */
	@BeforeAll
	public static void init() {

		// keys will be written into this location after init
		File keyFile = new File("./target/runtime/keyFile.json");

		HousekeepingConfig hc = HousekeepingConfig.builder()
				.reachable(true)
				.pingHost(true)
				.init(true) 
				.unseal(true)
				.unsealKeys(keyFile) // if unseal is true, keys and root token will be written here
				.build();

		System.out.println("housekeepingConfig: " + hc.toString());

		// LoginConfig in this case is handled internally (as we are doing an init and unseal). 
		TokeDriverConfig config = TokeDriverConfig.builder()
				.proto("http")
				.host("127.0.0.1")
				.port(8201)
				.kvName("toke-kv1")
				.kv2Name("toke-kv2")
				.housekeepingConfig(hc).build();

		System.out.println("TokenDriverConfig: " + config.toString());

		driver = new TokeDriver(config);

	}

	@Test
	public void testUserPass() {

		assertTrue(driver != null);
		
		// once this completes, the driver has logged itself in using the root token from the init, and a child token 
		// has been distributed to the various modules, as they are EventListeners
		driver.isReady();
		Auth auth = driver.auth();
		
		// have the child token from the TOKEN type login done by root 
		Sys sys = driver.sys();
		KVv1 kv = driver.kv();

		try {

			// 1. As child of root token
			
			// enable an authentication method called "userpass"
			AuthSpec userpassSpec = AuthSpec.builder("userpass", AuthType.USERPASS).build();
			sys.enableAuthMethod(userpassSpec);
			
			// create a user named bob with password, "password1"
			UserSpec user = UserSpec.builder("bob").authPath("userpass").password("password1").build();
			UserPass up = auth.userPass();
			up.createUpdateUser(user);
			Toke t = up.readUser("bob", "userpass");
			UserDataResponseDecorator userData = new UserDataResponseDecorator(t);
			System.out.println("bob: " + userData.toString());
			
			// write a policy for our user, bob, this allows him to among other things, read and write on toke-kv1
			
			sys.writePolicy("bob", new File("./test-materials/bob.policy.hcl"));
			t = sys.readPolicy("bob");
			PolicyResponseDecorator pd = new PolicyResponseDecorator(t);
			assertEquals("bob", pd.name);
			assertNotNull(pd.rules);
			System.out.println(pd.rules);
			
			// now enable a secrets engine related to the above policy and user
			SecretsEngineSpec kv1Spec = SecretsEngineSpec.builder("toke-kv1", SecretsEngineType.KV).build();
			t = sys.enableSecretsEngine("toke-kv1", kv1Spec);
			if(t.successful) {
				System.err.println(t.response);
			}else {
				System.err.println(t.response);
				fail();
			}
			
		  // 2. Now login as 'bob', we want to write values using his token
			
		 LoginConfig bobLoginConfig = LoginConfig.builder(AuthType.USERPASS).username("bob").password("password1").build();
		 try {
			auth.login(bobLoginConfig);
		} catch (LoginFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

		  // now try writing some values 
			
		  kv.write("toke-kv1/testcase", "key0", "value0");
		  kv.accumulate("toke-kv1/testcase", "key1", "value1");
		  kv.accumulate("toke-kv1/testcase", "key2", "value2");
		  
		 System.err.println("found"+ kv.list("toke-kv1"));
		 
		 System.err.println("found"+ kv.kvRead("toke-kv1/testcase").accessor().json().toString(4));
			

		} catch (WriteException e) {
			e.printStackTrace();
			fail();
		} catch (ReadException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigureException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testAppRole() {

		assertTrue(driver != null);
		driver.isReady();
		Sys sys = driver.sys();

		try {

			AuthSpec approleSpec = AuthSpec.builder("approle", AuthType.APPROLE).build();
			sys.enableAuthMethod(approleSpec);

			sys.writePolicy("bob", new File("./test-materials/bob.policy.hcl"));
			Toke t = sys.readPolicy("bob");
			PolicyResponseDecorator prd = new PolicyResponseDecorator(t);
			assertEquals("bob", prd.name);
			assertNotNull(prd.rules);
			System.out.println(prd.rules);

	//		AppRoleSpec approle = AppRoleSpec.builder("bob-builder-approle");
		


		} catch (WriteException e) {
			e.printStackTrace();
			fail();
		} catch (ReadException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigureException e) {
			e.printStackTrace();
			fail();
		}
	}

}
