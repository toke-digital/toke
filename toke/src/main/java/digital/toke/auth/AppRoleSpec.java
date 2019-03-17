package digital.toke.auth;

import org.json.JSONObject;

public class AppRoleSpec {
	
	String authPath; // the approle auth path, normally "approle". 
	
	String roleName; // name of the app role
	boolean bindSecretId;  // make secret_id required if true
	String policies;
	String ttl;
	String maxTTL;
	String period; // causes token to become periodic
	String tokenType; // service, batch, or default
	
	// TODO the CIDRs and some other arcane fields
	

	public AppRoleSpec(String roleName) {
			this.roleName = roleName;
	}
	
	public static Builder builder(String roleName) {
		return new Builder(roleName);
	}
	
	public static class Builder {
		
		String authPath;
		
		String roleName; // name of the app role
		boolean bindSecretId;  // make secret_id required if true
		String policies;
		String ttl;
		String maxTTL;
		String period; // causes token to become periodic if set
		String tokenType; // service, batch, or default
		
		 private Builder() {}
		 public Builder(String roleName) {
			 this();
			 this.roleName = roleName;
			 policies = "default";
			 authPath = "approle"; // default
			 tokenType = "default"; // default
		 }
		 
		 public Builder authPath(String authPath) {
			 this.authPath = authPath;
			 return this;
		 }
		 
		 public Builder period(String period) {
			 this.period = period;
			 return this;
		 }
		 
		 public Builder bindSecretId(boolean bind) {
			 this.bindSecretId = bind;
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
		 
		 public AppRoleSpec build() {
			 AppRoleSpec approle = new AppRoleSpec(roleName);
			 approle.authPath = this.authPath;
			 approle.bindSecretId = this.bindSecretId;
			 approle.policies = this.policies;
			 approle.maxTTL = this.maxTTL;
			 approle.ttl = this.ttl;
			 approle.tokenType = this.tokenType;
			 approle.period = this.period;
			 
			 return approle;
		 }
	}

	

	/**
	 * Return as JSON for use in createUpdateApprole(). Values will not be serialized if not present
	 */
	public String toString() {
		
		JSONObject obj = new JSONObject();
	
		obj.putOpt("role_name", roleName);
		obj.putOpt("bind_secret_id", bindSecretId);
		obj.putOpt("policies", policies);
		obj.putOpt("ttl", ttl);
		obj.putOpt("max_ttl", maxTTL);
		obj.putOpt("token_type", tokenType);
		obj.putOpt("period", period);
	
		
		return obj.toString();
	}

}
