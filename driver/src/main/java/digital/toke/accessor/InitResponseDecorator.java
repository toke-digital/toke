package digital.toke.accessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * typical response data:
 * 
 * {
    "keys": ["one", "two", "three"],
    "keys_base64": ["cR9No5cBC", "F3VLrkOo", "zIDSZNGv"],
    "root_token": "foo"
   }
 */
public class InitResponseDecorator extends TokeResponseDecorator {
	
	public InitResponseDecorator(Toke toke) {
		 super(toke);
	}
	
	public List<String> keys() {
		JSONArray array = toke.accessor().json().getJSONArray("keys");
		ArrayList<String> list = new ArrayList<String>();
		array.forEach(item -> list.add(String.valueOf(item)));
		return list;
	}

	public List<String> keysBase64() {
		JSONArray array = toke.accessor().json().getJSONArray("keys_base64");
		ArrayList<String> list = new ArrayList<String>();
		array.forEach(item -> list.add(String.valueOf(item)));
		return list;
	}
	
	public String rootToken() {
		return toke.accessor().json().getString("root_token");
	}
	
	//Write keys from response to a file 
	public void writeKeysToFile(File keyFile) {
		
		try {
			Files.write(keyFile.toPath(), toke.accessor().json().toString(4).getBytes("UTF-8"));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		
	}
}
