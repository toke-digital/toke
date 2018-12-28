package digital.toke.accessor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * if "successful" from the driver comes back false, an instance of this class is often returned. 
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Choke extends Toke {

	public Choke(int code, boolean successful, String response) {
		super(code, successful, response);
	}
	
	public List<Object> errors() {
		JSONArray array = new JSONObject(response).optJSONArray("errors");
		if(array != null) return array.toList();
		else return new ArrayList<Object>();
	}

}
