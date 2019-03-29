/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

/**
 * A "toke" is a simple wrapper on the response out of OKHTTP with HTTPResponse code, success flag, and message body.
 * 
 * Toke object has an unbound JSON body which needs to be interpreted. We use Accessor objects for this purpose. 
 * There is a general "Accessor" object provided in Toke itself: toke.getAccessor().json() returns the a JSONObject binding.
 * 
 * There are also Decorators 
 * 
 * If you prefer a more dynamic approach you can use the toke-path module which has json-path integration; but at the
 * cost of several additional jar files. 
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class Toke {
	
	final Accessor accessor;

	// the HTTP Response code
	public final int code;

	// if code in range of 200-300 then true
	public final boolean successful;

	// this will contain vault standard error object if successful is false, but
	// should at least always be valid json
	public final String response;

	public Toke(int code, boolean successful, String response) {
	
		this.code = code;
		this.successful = successful;

		// if not successful, body probably looks something like this:
		//
		// {"errors": ["ldap operation failed"]}

		this.response = response;
		
		accessor = new Accessor(this);

	}

	public String toString() {
		return String.format("TokeResponse: %d, %s, %s", code,successful,response);
	}

	public Accessor accessor() {
		return accessor;
	}

}
