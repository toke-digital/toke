package com.mockumatrix.toke.accessor;

/**
 * Simple wrapper on the response out of OKHTTP
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
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

		// if not successful, body might look like this:
		//
		// {"errors": ["ldap operation failed"]}

		this.response = response;

	}
	
	private Data data;
	private Secrets list;
	private Capabilities caps;

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
	 * Use weith KVv1 and KVv2 list secrets
	 * @return
	 */
	public Secrets kvList() {
		if(list == null) list = new Secrets(this);
		return list;
	}
	
	
	public String toString() {
		return String.format("TokeResponse: %d, %s, %s", code,successful,response);
	}

}
