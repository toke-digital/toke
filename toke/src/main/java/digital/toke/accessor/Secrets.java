package digital.toke.accessor;

import java.util.ArrayList;
import java.util.List;

public class Secrets extends Accessor {

	public Secrets(Toke resp) {
		super(resp);
	}
	
	public List<String> secrets() {
		List<Object> list = json().getJSONObject("data").getJSONArray("keys").toList();
		List<String> newList = new ArrayList<String>();
		list.forEach(item-> newList.add(String.valueOf(item)));
		return newList;
	}

}
