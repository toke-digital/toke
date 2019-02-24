package digital.toke.policy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PolicyTest {
	
	@Test
	public void test() {
		
		List<CapabilityEnum> list = new ArrayList<CapabilityEnum>();
		list.add(CapabilityEnum.create);
		list.add(CapabilityEnum.delete);
		list.add(CapabilityEnum.read);
		Policy p = new Policy.Builder().withPath("secret/something").withCapabilities(list).build();
		System.err.println(p.toString());
	}

}
