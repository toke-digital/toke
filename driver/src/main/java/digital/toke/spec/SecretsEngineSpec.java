/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.spec;

import java.util.List;

import org.json.JSONObject;

public class SecretsEngineSpec {
	
	String path;
	SecretsEngineType type;
	String description;
	String defaultLeaseTTL;
	String maxLeaseTTL;
	boolean forceNoCache;
	List<String> auditNonHmacRequestKeys;
	List<String> auditNonHmacResponseKeys; 
	String listingVisibility; // unauth or hidden, default is hidden 
	List<String> passthroughRequestHeaders;
	List<String> allowedResponseHeaders; 
	
	String version; // 1 or 2, currently, for kv, default is 1

	// enterprise only
	boolean local;
	boolean seal_wrap;
	
	public static Builder builder(String path, SecretsEngineType type) {
		return new Builder(path, type);
	}
	
	public static class Builder {
		
		String path;
		SecretsEngineType type;
		String description;
		String defaultLeaseTTL;
		String maxLeaseTTL;
		boolean forceNoCache;
		List<String> auditNonHmacRequestKeys;
		List<String> auditNonHmacResponseKeys; 
		ListingVisibility listingVisibility; // unauth or hidden, default is hidden 
		List<String> passthroughRequestHeaders;
		List<String> allowedResponseHeaders; 
		
		String version; // 1 or 2, currently, for kv, default is 1

		// enterprise only
		boolean local;
		boolean seal_wrap;
		
		public Builder(String path, SecretsEngineType type) {
			this.path = path;
			this.type = type;
		}
		
		public Builder withDescription(String desc) {
			this.description = desc;
			return this;
		}
		
		public Builder withDefaultLeaseTTL(String ttl) {
			this.defaultLeaseTTL = ttl;
			return this;
		}
		
		public Builder withMaxLeaseTTL(String ttl) {
			this.maxLeaseTTL = ttl;
			return this;
		}
		
		public Builder withForceNoCache(boolean bool) {
			this.forceNoCache= bool;
			return this;
		}
		
		public Builder withAuditNonHmacRequestKeys(List<String> keys) {
			this.auditNonHmacRequestKeys = keys;
			return this;
		}
		
		public Builder withAuditNonHmacResponseKeys(List<String> keys) {
			this.auditNonHmacRequestKeys = keys;
			return this;
		}
		
		public Builder withListingVisibility(ListingVisibility lv) {
			this.listingVisibility = lv;
			return this;
		}
		
		public enum ListingVisibility {
			hidden, unauth;
		}
		
		public Builder withPassThrouhgRequestHeaders(List<String> headers) {
			this.passthroughRequestHeaders = headers;
			return this;
		}
		
		public Builder allowedResponseHeaders(List<String> headers) {
			this.allowedResponseHeaders = headers;
			return this;
		}
		
		public SecretsEngineSpec build() {
			SecretsEngineSpec mp = new SecretsEngineSpec(path,type);
			mp.version = version;
			mp.defaultLeaseTTL = defaultLeaseTTL;
			mp.maxLeaseTTL = maxLeaseTTL;
			mp.description = description;
			
		// TODO
			
			return mp;
		}
		
	}
	

	public SecretsEngineSpec(String path, SecretsEngineType type) {
		this();
		this.path = path;
		this.type = type;
	}
	
	private SecretsEngineSpec() {}
	
	public String toString() {
		
		// TODO 
		
		JSONObject obj = new JSONObject();
		obj.put("type", type.getType());
		
		JSONObject config = new JSONObject();
		JSONObject options = new JSONObject();
		options.put("version", version);
		config.put("config", options);
		
		
		
		return obj.toString();
	}

}
