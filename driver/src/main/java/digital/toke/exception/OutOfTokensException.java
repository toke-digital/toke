/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

/**
 * Thrown when the TokenManager is unhappy
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class OutOfTokensException extends TokeException {

	private static final long serialVersionUID = 1L;

	public OutOfTokensException() {}

	public OutOfTokensException(String arg0) {
		super(arg0);
	}

	public OutOfTokensException(Throwable arg0) {
		super(arg0);
	}


}
