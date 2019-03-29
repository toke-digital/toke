package digital.toke.accessor;

import org.json.JSONObject;

public abstract class TokeResponseDecorator {

	final Toke toke;

	public TokeResponseDecorator(Toke toke) {
		super();
		this.toke = toke;
	}
	
	public JSONObject json() {
		return toke.accessor().json();
	}
	
}
