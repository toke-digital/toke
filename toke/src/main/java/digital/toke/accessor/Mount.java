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
 *
 */
public class Mount {

	public String name;
	public String type;
	public String description;
	public long defaultLeaseTTL;
	public long maxLeaseTTL;
	public boolean forceNoCache;
	public boolean sealWrap;
	
	Mount(String name, JSONObject obj) {
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

}
