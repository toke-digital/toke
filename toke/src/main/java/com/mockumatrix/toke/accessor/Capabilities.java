package com.mockumatrix.toke.accessor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

/**
 * Use with response from /sys/capabilities
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Capabilities extends Accessor {

	public Capabilities(Toke resp) {
		super(resp);
	}
	
	public List<String> list() {
		List<String> list = new ArrayList<String>();
		json().getJSONArray("capabilities")
		   .forEach(item -> list.add(String.valueOf(item)));
		return list;
	}
	
	public List<String> forPath(String path) {
		List<String> list = new ArrayList<String>();
		JSONArray array = json().optJSONArray(path);
		 if(array == null) return list;
		 else {
			 array.forEach(item -> list.add(String.valueOf(item)));
		 }
		return list;
	}

}
