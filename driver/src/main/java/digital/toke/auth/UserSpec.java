package digital.toke.auth;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class UserSpec {
	
	String authPath; // the username auth path, normally "userpass". 
	
	String username;
	String password;
	String policies;
	String ttl;
	String maxTTL;
	List<String> boundCidrs;

	public UserSpec(String username) {
			this.username = username;
	}
	
	public static Builder builder(String username) {
		return new Builder(username);
	}
	
	public static class Builder {
		
		String authPath;
		
		String username; // make your life easy and use URL-safe characters in usernames, as they are part of a path
		
		String password;
		
		String policies;
		// The lease duration for this user
		String ttl;
		// maximum duration for which the user's login will exire
		String maxTTL;
		List<String> boundCidrs;
		
		 private Builder() {}
		 public Builder(String username) {
			 this();
			 this.username = username;
			 policies = "default";
			 boundCidrs = new ArrayList<String>();
			 authPath = "userpath"; // default
		 }
		 
		 public Builder authPath(String authPath) {
			 this.authPath = authPath;
			 return this;
		 }
		 
		 public Builder password(String password) {
			 this.password = password;
			 return this;
		 }
		 
		 /**
		  * Comma delimited list of policies this user will be bound with
		  * @param policiesList
		  * @return
		  */
		 public Builder policies(String policies) {
			 this.policies = policies;
			 return this;
		 }
		 
		 public Builder ttl(String ttl) {
			 this.ttl = ttl;
			 return this;
		 }
		 
		 public Builder maxTTL(String maxTTL) {
			 this.maxTTL = ttl;
			 return this;
		 }
		 
		 public Builder boundCidrs(List<String> cidrs) {
			 this.boundCidrs = cidrs;
			 return this;
		 }
		 
		 public UserSpec build() {
			 UserSpec user = new UserSpec(username);
			 user.authPath = this.authPath;
			 user.password = this.password;
			 user.policies = this.policies;
			 user.boundCidrs = this.boundCidrs;
			 user.maxTTL = this.maxTTL;
			 user.ttl = this.ttl;
			 
			 return user;
		 }
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPolicies() {
		return policies;
	}

	public String getTtl() {
		return ttl;
	}

	public String getMaxTTL() {
		return maxTTL;
	}

	public List<String> getBoundCidrs() {
		return boundCidrs;
	}
	
	public String getAuthPath() {
		return authPath;
	}

	/**
	 * Return as JSON for use in createUpdateUser(). Values will not be serialized if not present
	 */
	public String toString() {
		
		JSONObject obj = new JSONObject();
	
		obj.putOpt("password", password);
		
		obj.putOpt("policies", policies);
		obj.putOpt("ttl", ttl);
		obj.putOpt("max_ttl", maxTTL);
		if(boundCidrs != null) {
			for(String item: boundCidrs) {
				obj.append("bound_cidrs", item);
			}
		}
		
		return obj.toString();
	}

}
