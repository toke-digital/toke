/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

/**
 * Thrown when write-oriented calls fail
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class WriteException extends TokeException {

	private static final long serialVersionUID = 1L;

	public WriteException() {}

	public WriteException(String arg0) {
		super(arg0);
	}

	public WriteException(Throwable arg0) {
		super(arg0);
	}


}
