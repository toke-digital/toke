/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.accessor;

import org.json.JSONObject;

/**
 * We flatten out these fields:
 * 
 * "aws": {
    "type": "aws",
    "description": "AWS keys",
    "config": {
      "default_lease_ttl": 0,
      "max_lease_ttl": 0,
      "force_no_cache": false,
      "seal_wrap": false
    }
    
 * @author daves
 * @see MountsResponseDecorator
 */
public class MountVO {

	public final String name;
	public final String type;
	public final String description;
	public final long defaultLeaseTTL;
	public final long maxLeaseTTL;
	public final boolean forceNoCache;
	public final boolean sealWrap;
	
	MountVO(String name, JSONObject obj) {
		this.name = name;
		JSONObject inner = obj.getJSONObject(name);
		type = inner.optString("type");
		description = inner.optString("description");
		inner = obj.getJSONObject("config");
		defaultLeaseTTL = inner.optLong("default_lease_ttl");
		maxLeaseTTL = inner.optLong("max_lease_ttl");
		forceNoCache = inner.optBoolean("force_no_cache");
		sealWrap = inner.optBoolean("seal_wrap");
	}

	@Override
	public String toString() {
		return "MountVO [name=" + name + ", type=" + type + ", description=" + description + ", defaultLeaseTTL="
				+ defaultLeaseTTL + ", maxLeaseTTL=" + maxLeaseTTL + ", forceNoCache=" + forceNoCache + ", sealWrap="
				+ sealWrap + "]";
	}
	
	

}
