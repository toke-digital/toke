/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Base class for classes which implement the API calls
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Base {

	protected CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public Base() {}
	
	protected void latch() {
		try {
			countDownLatch.await(1L,TimeUnit.MINUTES);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
}
