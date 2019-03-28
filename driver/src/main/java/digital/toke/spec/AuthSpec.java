package digital.toke.spec;

import java.util.List;

import org.json.JSONObject;

import digital.toke.AuthType;
import digital.toke.spec.AuthSpec.Builder.ListingVisibility;

public class AuthSpec {
	
	String path;
	String type;
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
	
	public static Builder builder(String path, AuthType type) {
		return new Builder(path, type.toString());
	}
	
	
	
	public static class Builder {
		
		String path;
		String type;
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
		
		public Builder(String path, String type) {
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
		
		public AuthSpec build() {
			if(type == null) throw new RuntimeException("type cannot be null in an AuthSpec");
			AuthSpec mp = new AuthSpec(path,type);
	
			mp.allowedResponseHeaders = this.allowedResponseHeaders;
			mp.auditNonHmacRequestKeys = this.auditNonHmacRequestKeys;
			mp.auditNonHmacResponseKeys = this.auditNonHmacResponseKeys;
			mp.defaultLeaseTTL = this.defaultLeaseTTL;
			mp.description = this.description;
			mp.forceNoCache = this.forceNoCache;
			mp.listingVisibility = this.listingVisibility;
			mp.local = this.local;
			mp.maxLeaseTTL = this.maxLeaseTTL;
			mp.passthroughRequestHeaders = this.passthroughRequestHeaders;
			mp.path = this.path;
			mp.seal_wrap = this.seal_wrap;
			mp.type = this.type;
			mp.version = this.version;

			return mp;
		}
		
	}
	

	public AuthSpec(String path, String type) {
		this();
		this.path = path;
		this.type = type;
	}
	
	private AuthSpec() {}
	
	public String toString() {
		
		// TODO here - add the REST OF THE PARAMS
		
		
		JSONObject obj = new JSONObject();
		obj.put("type", type.toLowerCase());
		obj.putOpt("description", this.description);
		
		JSONObject config = new JSONObject();
		JSONObject options = new JSONObject();
		options.putOpt("version", version);
		config.putOpt("config", options);
		
		
		return obj.toString();
	}

	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultLeaseTTL() {
		return defaultLeaseTTL;
	}

	public String getMaxLeaseTTL() {
		return maxLeaseTTL;
	}

	public boolean isForceNoCache() {
		return forceNoCache;
	}

	public List<String> getAuditNonHmacRequestKeys() {
		return auditNonHmacRequestKeys;
	}

	public List<String> getAuditNonHmacResponseKeys() {
		return auditNonHmacResponseKeys;
	}

	public ListingVisibility getListingVisibility() {
		return listingVisibility;
	}

	public List<String> getPassthroughRequestHeaders() {
		return passthroughRequestHeaders;
	}

	public List<String> getAllowedResponseHeaders() {
		return allowedResponseHeaders;
	}

	public String getVersion() {
		return version;
	}

	public boolean isLocal() {
		return local;
	}

	public boolean isSeal_wrap() {
		return seal_wrap;
	}
	
	

}
