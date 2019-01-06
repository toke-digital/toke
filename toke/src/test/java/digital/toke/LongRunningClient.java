/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import digital.toke.accessor.Toke;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;

/**
 * Useful class for testing lifecycle
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class LongRunningClient {
	
	private static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

	public static void main(String [] args) {
		
		// create a token lifecycle configuration - most fields have sensible defaults
		// in this case unseal the vault if required or desired - not every use-case is as strict as the published (manual) approach
		HousekeepingConfig hc = null;
		try {
			File keyFile = new File("C:\\vault\\keys");
			hc = new HousekeepingConfig().unseal(true).unsealKeys(keyFile);
		}catch(IOException x) {
			x.printStackTrace();
		}
		
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.kvName("toke-kv1") 
				.kv2Name("toke-kv2")
				.authType("USERPASS")
				.username("bob")
				.password("password1")
				.housekeepingConfig(hc);
		
		// driver will auto-login 
		final Driver driver = new Driver(config);
		
		// now do something with Driver periodically
		
		scheduledPool.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				
				// some data to write
				JSONObject obj = new JSONObject()
						.put("data", new JSONObject()
								.put("key0", 100)
								.put("key1", "value1"));
				
				try {
					Toke toke = driver.kv2().kvWrite("test/stuff", obj);
					System.out.println(toke);
				} catch (WriteException e) {
					//e.printStackTrace();
				}
				
				try {
					Toke toke = driver.kv2().kvRead("test/stuff");
					System.out.println(toke);
				} catch (ReadException e) {
				//	e.printStackTrace();
				}
				
			}
			
		}, 1, 30, TimeUnit.SECONDS);
		
		
		
		// blocks so we don't exit main
		 try {
		        Object lock = new Object();
		        synchronized (lock) {
		            while (true) {
		                lock.wait();
		            }
		        }
		    } catch (InterruptedException ex) {
		    }
	}

}
