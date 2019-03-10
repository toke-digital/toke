/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

/**
 * A "toke" is a simple wrapper on the response out of OKHTTP with HTTPResponse code, success flag, and message body.
 * 
 * Toke object has an unbound JSON body which needs to be interpreted. We use Accessor objects for this purpose. 
 * Each Accessor knows how to bind a json string and also has accessor methods appropriate to our needs in 
 * that specific API response.
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class Toke {

	// the HTTP Response code
	public final int code;

	// if code in range of 200-300 then true
	public final boolean successful;

	// this will contain vault standard error object if successful is false, but
	// should be valid json
	public final String response;

	public Toke(int code, boolean successful, String response) {
		super();
		this.code = code;
		this.successful = successful;

		// if not successful, body probably looks something like this:
		//
		// {"errors": ["ldap operation failed"]}

		this.response = response;

	}
	
	private Data data;
	private Secrets list;
	private Capabilities caps;
	private Init init;
	private Policy policy;
	private UserData userData;
	
	public Init init() {
		if(init == null) init = new Init(this);
		return init;
	}

	/**
	 * Use with KVv1 and KVv2 reads
	 * @return
	 */
	public Data data() {
		if(data == null) data = new Data(this);
		return data;
	}
	
	/**
	 * Use with sys/capabilities
	 * @return
	 */
	public Capabilities caps() {
		if(caps == null) caps = new Capabilities(this);
		return caps;
	}
	
	/**
	 * Use with KVv1 and KVv2 to list secrets
	 * @return
	 */
	public Secrets kvList() {
		if(list == null) list = new Secrets(this);
		return list;
	}
	
	public Policy policy() {
		if(policy == null) policy = new Policy(this);
		return policy;
	}
	
	public UserData userData() {
		if(userData == null) userData = new UserData(this);
		return userData;
	}
	
	
	public String toString() {
		return String.format("TokeResponse: %d, %s, %s", code,successful,response);
	}

}
