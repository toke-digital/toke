package digital.toke.accessor;

import org.json.JSONObject;

/**
 * Parent class of Accessors. These are sort of a cross between a Decorator and an Adapter pattern design.
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Accessor {

	public final Toke toke;

	public Accessor(Toke resp) {
		this.toke = resp;
	}
	
	public JSONObject json() {
		return new JSONObject(toke.response);
	}
}
