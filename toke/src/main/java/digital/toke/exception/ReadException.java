/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

/**
 * Thrown when a read-oriented call fails
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class ReadException extends TokeException {

	private static final long serialVersionUID = 1L;

	public ReadException() {}

	public ReadException(String arg0) {
		super(arg0);
	}

	public ReadException(Throwable arg0) {
		super(arg0);
	}


}
