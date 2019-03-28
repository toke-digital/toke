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

import digital.toke.accessor.Toke;
import digital.toke.auth.UserPass;
import digital.toke.auth.UserSpec;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;
import digital.toke.spec.AuthSpec;

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

		HousekeepingConfig hc = HousekeepingConfig.builder().reachable(true).pingHost(true).init(true) // special case,
																										// on success
																										// root token
																										// will be
																										// inserted into
																										// config.token
																										// as it is
																										// assumed we
																										// will need it
				.unseal(true).unsealKeys(keyFile) // if unseal is true, keys and root token will be written here
				.build();

		System.out.println("housekeepingConfig: " + hc.toString());

		TokeDriverConfig config = TokeDriverConfig.builder().proto("http").host("127.0.0.1").port(8201)
				.kvName("toke-kv1").kv2Name("toke-kv2").authType("TOKEN") // see AuthType enum for the suported types
				.housekeepingConfig(hc).build();

		System.out.println("housekeepingConfig: " + config.toString());

		driver = new TokeDriver(config);

	}

	@Test
	public void testUserPass() {

		assertTrue(driver != null);
		driver.isReady();
		Sys sys = driver.sys();

		try {

			AuthSpec userpassSpec = AuthSpec.builder("my-userpass", AuthType.USERPASS).build();
			sys.enableAuthMethod(userpassSpec);

			sys.writePolicy("bob", new File("./test-materials/bob.policy.hcl"));
			Toke t = sys.readPolicy("bob");
			assertEquals("bob", t.policy().name);
			assertNotNull(t.policy().rules);
			System.out.println(t.policy().rules);

			UserSpec user = UserSpec.builder("bob").authPath("my-userpass").password("password1").build();
			UserPass up = driver.auth().userPass();
			up.createUpdateUser(user);
			t = up.readUser("bob", "my-userpass");
			System.out.println("bob: " + t.userData().toString());


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
			assertEquals("bob", t.policy().name);
			assertNotNull(t.policy().rules);
			System.out.println(t.policy().rules);

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
