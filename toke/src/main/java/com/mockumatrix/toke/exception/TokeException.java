package com.mockumatrix.toke.exception;

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
