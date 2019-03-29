/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SealStatusResponseDecorator extends TokeResponseDecorator {

	private static final Logger logger = LogManager.getLogger(SealStatusResponseDecorator.class);
	
	public SealStatusResponseDecorator(Toke resp) {
		super(resp);
		if(resp == null) logger.error("Response was null...?");
	}
	
	public boolean isSealed() {
		return json().optBoolean("sealed", true);
	}
	
	/**
	 * @return the threshhold or -1 if not present in response
	 */
	public int threshhold() {
		return json().optInt("t", -1);
	}
	
	/**
	 * @return the number of shares or -1 if not present in response
	 */
	public int numberOfShares() {
		return json().optInt("n", -1);
	}
	
	@Override
	public String toString() {
		return toke.toString();
	}

}
