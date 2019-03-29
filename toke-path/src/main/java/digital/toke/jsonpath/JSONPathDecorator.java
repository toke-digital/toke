package digital.toke.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import digital.toke.accessor.Toke;
import digital.toke.accessor.TokeResponseDecorator;

/**
 * Just glues our Toke object to a JsonPath ReadContext.
 * See  https://github.com/json-path/JsonPath
 * 
 * 
 * @author daves
 *
 */
public class JSONPathDecorator extends TokeResponseDecorator {

	Object document;
	
	public JSONPathDecorator(Toke toke) {
		super(toke);
		document = Configuration.defaultConfiguration().jsonProvider().parse(toke.response);
	}
	
	public String readString(String expression) {
		return JsonPath.read(document, expression);
	}
	
	/**
	 * use like this: get().read(<some expression>); 
	 * 
	 * @return
	 */
	public ReadContext get() {
		return JsonPath.using(Configuration.defaultConfiguration()).parse(document);
	}

}
