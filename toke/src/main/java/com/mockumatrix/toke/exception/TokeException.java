package com.mockumatrix.toke.exception;

/**
 * Superclass for Exceptions from out of the driver
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
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
