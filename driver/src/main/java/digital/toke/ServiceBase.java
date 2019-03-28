/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import digital.toke.accessor.Toke;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.ReadException;
import digital.toke.exception.WriteException;

/**
 * Base class for classes which implement the API calls
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class ServiceBase {

	protected CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public ServiceBase() {}
	
	protected void latch() {
		try {
			countDownLatch.await(10L,TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	protected void countDown() {
		countDownLatch.countDown();
	}
	
	protected void refreshLatch() {
		countDownLatch = new CountDownLatch(1);
	}
	
	protected void readExceptionExcept(Toke response, int val) throws ReadException {
		if(response.code == val) return;
		if(response.code==404) throw new ReadException("Http 404 - this is usually a problem with the path.");
	//	if(response.code==400) throw new ReadException("Http 400 - this is usually a permissions issue.");
		if(response.code==403) throw new ReadException("Http 403 - this is usually a permissions issue.");

		  throw new ReadException("Unexpected HTTP Response Code: "+response.code);

	}
	
	protected void writeExceptionExcept(Toke response, int val) throws WriteException {
		if(response.code == val) return;
		//System.err.println(response.response);
		if(response.code==400) throw new WriteException("Http 400 - in kv2 this may be a check-and-set issue.");
		if(response.code==404) throw new WriteException("Http 404 - this is usually a problem with the path.");
		if(response.code==403) throw new WriteException("Http 403 - this is usually a permissions issue.");

		  throw new WriteException("Unexpected HTTP Response Code: "+response.code);

	}
	
	protected void configureExceptionExcept(Toke response, int val) throws ConfigureException {
		if(response.code == val) return;
		if(response.code==404) throw new ConfigureException("Http 404 - this is usually a problem with the path.");
		if(response.code==400) throw new ConfigureException("Http 400 - this is usually a permissions issue.");

		  throw new ConfigureException("Unexpected HTTP Response Code: "+response.code);

	}
	
}
