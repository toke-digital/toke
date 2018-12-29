/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

/**
 * Periodically fired to maintain health of tokens.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Housekeeping implements Runnable {

	Auth auth;

	public Housekeeping(Auth auth) {
		super();
		this.auth = auth;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	


}
