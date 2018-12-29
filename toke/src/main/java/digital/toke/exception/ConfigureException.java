/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.exception;

/**
 * Thrown when a configuration-oriented call fails
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class ConfigureException extends TokeException {

	private static final long serialVersionUID = 1L;

	public ConfigureException() {}

	public ConfigureException(String arg0) {
		super(arg0);
	}

	public ConfigureException(Throwable arg0) {
		super(arg0);
	}


}
