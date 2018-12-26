package com.mockumatrix.toke;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mockumatrix.toke.event.EventEnum;
import com.mockumatrix.toke.event.TokenEvent;
import com.mockumatrix.toke.event.TokenListener;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Thread safe wrapper on the HTTP calls
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 * @see APIResponse
 *
 */
public class Networking implements TokenListener {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	protected final Lock lock = new ReentrantLock();
	protected final OkHttpClient client;
	protected Token token;

	public Networking() {
		client = new OkHttpClient();
	}

	/**
	 * X-Vault-Token will be added as header. Care must be taken not to call this method prior to
	 * successful login.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public APIResponse get(String url) throws IOException {
		lock.lock();
		try {
			Request request = new Request.Builder()
					.url(url)
					.header("X-Vault-Token", token.clientToken())
					.build();
			
			int code; boolean success; String result;
			try (Response response = client.newCall(request).execute()){
				 result = response.body().string();
				 code = response.code();
				 success = response.isSuccessful();
			}
			
			return new APIResponse(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	public APIResponse delete(String url) throws IOException {
		lock.lock();
		try {
			Request request = new Request.Builder()
					.delete()
					.url(url)
					.header("X-Vault-Token", token.clientToken())
					.build();
			
			int code; boolean success; String result;
			try (Response response = client.newCall(request).execute()){
				 result = response.body().string();
				 code = response.code();
				 success = response.isSuccessful();
			}
			
			return new APIResponse(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	public APIResponse list(String url) throws IOException {
		lock.lock();
		try {
			
			// any list url needs to end with a slash
			if(!url.endsWith("/")) url += "/";
			
			Request request = new Request.Builder()
					.url(url+"?list=true")
					.header("X-Vault-Token", token.clientToken())
					.build();
			
			int code; boolean success; String result;
			try (Response response = client.newCall(request).execute()){
				 result = response.body().string();
				 code = response.code();
				 success = response.isSuccessful();
			}
			
			return new APIResponse(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Special case, does not get X-Vault-Token header, auth endpoints are unauthenticated
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public APIResponse login(String url, String json) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder().url(url).post(body).build();
			try (Response response = client.newCall(request).execute()) {
				return new APIResponse(response.code(), response.isSuccessful(), response.body().string());
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Special case, login using token which will call auth/token/create
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public APIResponse loginToken(String url, String json, String clientToken) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.header("X-Vault-Token", clientToken)
					.build();
			try (Response response = client.newCall(request).execute()) {
				return new APIResponse(response.code(), response.isSuccessful(), response.body().string());
			}
		} finally {
			lock.unlock();
		}
	}
	

	/**
	 * X-Vault-Token header will be set, care must be taken not to call this method until successful login.
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public APIResponse post(String url, String json) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.header("X-Vault-Token", token.clientToken())
					.build();
			try (Response response = client.newCall(request).execute()) {
				return new APIResponse(response.code(), response.isSuccessful(), response.body().string());
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
		}
		
	}
	

}
