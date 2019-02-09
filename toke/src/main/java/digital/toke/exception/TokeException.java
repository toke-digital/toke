/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

/**
 * Superclass for Exceptions from out of the driver. We throw hard exceptions rather than
 * runtime exceptions because it's import to be able to react to the Driver's signals
 * and try to recover
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class TokeException extends Exception {

	private static final long serialVersionUID = 1L;

	public TokeException() {}

	public TokeException(String message) {
		super(message);
	}

	public TokeException(Throwable cause) {
		super(cause);
	}

}
