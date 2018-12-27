package com.mockumatrix.toke.response;

import java.util.Map;

public class BasicResponse extends APIResponseBase implements APIResponse {

	public BasicResponse(int code, boolean successful, String response) {
		super(code, successful, response);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Object> data() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> metadata() {
		throw new UnsupportedOperationException();
	}

}
