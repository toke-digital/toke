package digital.toke.accessor;

public class UserData extends Accessor {

	public UserData(Toke resp) {
		super(resp);
	}
	
	public String requestId() {
		return json().getString("request_id");
	}
	
	public String leaseId() {
		return json().getString("lease_id");
	}
	
	public long leaseDuration() {
		return json().getLong("lease_duration");
	}
	
	public boolean renewable() {
		return json().getBoolean("renewable");
	}
	
	public long ttl() {
		return json().getJSONObject("data").getLong("ttl");
	}
	
	public long maxTTL() {
		return json().getJSONObject("data").getLong("max_ttl");
	}
	
	
	public String policies() {
		return json().getString("policies");
	}

}
