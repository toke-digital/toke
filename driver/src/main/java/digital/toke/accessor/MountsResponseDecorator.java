package digital.toke.accessor;

import java.util.Iterator;

/**
 <pre> Example output
 
 {
  "aws": {
    "type": "aws",
    "description": "AWS keys",
    "config": {
      "default_lease_ttl": 0,
      "max_lease_ttl": 0,
      "force_no_cache": false,
      "seal_wrap": false
    }
  },
  "sys": {
    "type": "system",
    "description": "system endpoint",
    "config": {
      "default_lease_ttl": 0,
      "max_lease_ttl": 0,
      "force_no_cache": false,
      "seal_wrap": false
    }
  }
}
 </pre>

 
 */

public class MountsResponseDecorator extends TokeResponseDecorator {

	public MountsResponseDecorator(Toke resp) {
		super(resp);
	}
	
	public boolean contains(String name) {
		Iterator<String> keys = this.json().keys();
		while(keys.hasNext()) {
			if(keys.next().equals(name)) return true;
		}
		
		return false;
	}
	
	public MountVO getMount(String name) {
		MountVO m = new MountVO(name, json());
		return m;
		
	}

}
