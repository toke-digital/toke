/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Thread-safe wrapper on the HTTP calls, we are using the delightful OKHttp. 
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 * @see Toke
 *
 */
public class Networking implements TokenListener {

	private static final Logger logger = LogManager.getLogger(Networking.class);
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	protected final Lock lock = new ReentrantLock();
	protected final OkHttpClient client;
	protected Token token;

	public Networking() {
		client = new OkHttpClient();
		logger.info("Initialized a networking instance");
	}

	/**
	 * X-Vault-Token will be added as header. Care must be taken not to call this method prior to
	 * successful login (or more specifically having a valid token set).
	 * 
	 * @param url
	 * @return a response containing a code, success flag, and the body as a String
	 * @throws IOException
	 */
	public Toke get(String url) throws IOException {
		return get(url, true);
	}
	
	public Toke get(String url, boolean withXVaultToken) throws IOException {
		lock.lock();
		try {
			Request request = null;
			
			if(withXVaultToken) {
				request = new Request.Builder()
					.url(url)
					.header("X-Vault-Token", token.clientToken())
					.build();
			}else {
				request = new Request.Builder()
						.url(url)
						.build();
			}
			
			int code; boolean success; String result;
			try (Response response = client.newCall(request).execute()){
				 result = response.body().string();
				 code = response.code();
				 success = response.isSuccessful();
			}
			
			return new Toke(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	public Toke delete(String url) throws IOException {
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
			
			return new Toke(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * List, always a special case!!
	 * 
	 * @param url
	 * @return a response containing a code, success flag, and the body as a String
	 * @throws IOException
	 */
	public Toke list(HttpUrl url) throws IOException {
		lock.lock();
		try {
			
			Request request = new Request.Builder()
					.url(url)
					.header("X-Vault-Token", token.clientToken())
					.build();
			
			logger.debug(request.toString());
			
			int code; boolean success; String result;
			try (Response response = client.newCall(request).execute()){
				 result = response.body().string();
				 code = response.code();
				 success = response.isSuccessful();
			}
			
			return new Toke(code, success, result);
			
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Special case, does not get X-Vault-Token header, auth endpoints are unauthenticated
	 * 
	 * @param url
	 * @param json
	 * @return a response containing a code, success flag, and the body as a String
	 * @throws IOException
	 */
	public Toke login(String url, String json) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder().url(url).post(body).build();
			try (Response response = client.newCall(request).execute()) {
				return new Toke(response.code(), response.isSuccessful(), response.body().string());
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
	 * @return a response containing a code, success flag, and the body as a String
	 * @throws IOException
	 */
	public Toke loginToken(String url, String json, String clientToken) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.header("X-Vault-Token", clientToken)
					.build();
			try (Response response = client.newCall(request).execute()) {
				return new Toke(response.code(), response.isSuccessful(), response.body().string());
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
	 * @return a response containing a code, success flag, and the body as a String
	 * @throws IOException
	 */
	public Toke post(String url, String json) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.header("X-Vault-Token", token.clientToken())
					.build();
			try (Response response = client.newCall(request).execute()) {
				return new Toke(response.code(), response.isSuccessful(), response.body().string());
			}
		} finally {
			lock.unlock();
		}
	}
	
	public Toke put(String url, String json, boolean withXVaultToken) throws IOException {
		lock.lock();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = null;
			
			if(withXVaultToken) request = new Request.Builder()
					.url(url)
					.put(body)
					.header("X-Vault-Token", token.clientToken())
					.build();
			else request = new Request.Builder()
					.url(url)
					.put(body)
					.build();
			
			
			try (Response response = client.newCall(request).execute()) {
				return new Toke(response.code(), response.isSuccessful(), response.body().string());
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * This class is downstream from the service classes, so no need to latch
	 */
	@Override
	public void tokenEvent(TokenEvent evt) {
		if(evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			logger.info("Token with accessor "+token.accessor()+" set on Networking instance");
		}
		
		if(evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
			logger.info("Reloaded token on Networking instance.");
		}
	}
}
