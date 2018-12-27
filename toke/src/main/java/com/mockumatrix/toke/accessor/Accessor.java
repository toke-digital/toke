package com.mockumatrix.toke.accessor;

import org.json.JSONObject;

public class Accessor {

	public final Toke response;

	public Accessor(Toke resp) {
		this.response = resp;
	}
	
	public JSONObject json() {
		return new JSONObject(response);
	}
}
