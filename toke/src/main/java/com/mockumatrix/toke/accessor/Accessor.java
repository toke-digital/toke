package com.mockumatrix.toke.accessor;

import org.json.JSONObject;

public class Accessor {

	public final Toke toke;

	public Accessor(Toke resp) {
		this.toke = resp;
	}
	
	public JSONObject json() {
		return new JSONObject(toke.response);
	}
}
