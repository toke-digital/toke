/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * auth/token/create* has parameters, which we encapsulate here
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 * @see TokeDriverConfig
 */
public class CreateTokenParameterSpec {
	
	String tokenHandle;
	String roleName;
	List<String> policies;
	JSONObject meta;
	boolean renewable;
    String ttl;
    String explicitMaxTTL;
    int numUses;
    String period;
    

	public CreateTokenParameterSpec() {
		// TODO Auto-generated constructor stub
	}
	
	public static Builder builder(String tokenHandle) {
		return new Builder(tokenHandle);
	}
	
	public static class Builder {
		
		String tokenHandle;
		String roleName;
		List<String> policies;
		JSONObject metadata;
		boolean renewable;
	    String ttl;
	    String explicitMaxTTL;
	    int numUses;
	    String period;
	    
		public Builder(String tokenHandle) {
			super();
		    this.tokenHandle = tokenHandle;
			metadata = new JSONObject();
			metadata.accumulate("token_handle", tokenHandle);
			policies = new ArrayList<String>();
			renewable = true;
			ttl = "1d";
			explicitMaxTTL = "1d";
			numUses = 0; // no limit
			
		}
		
		public Builder roleName(String roleName) {
			this.roleName = roleName;
			return this;
		}
	    
		
		// TODO
	    
	}

	public String getTokenHandle() {
		return tokenHandle;
	}

	public String getRoleName() {
		return roleName;
	}

	public List<String> getPolicies() {
		return policies;
	}

	public JSONObject getMeta() {
		return meta;
	}

	public boolean isRenewable() {
		return renewable;
	}

	public String getTtl() {
		return ttl;
	}

	public String getExplicitMaxTTL() {
		return explicitMaxTTL;
	}

	public int getNumUses() {
		return numUses;
	}

	public String getPeriod() {
		return period;
	}
		
	
	
	
}
