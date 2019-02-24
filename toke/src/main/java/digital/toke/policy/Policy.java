package digital.toke.policy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Policy {

	private String path;
	private List<CapabilityEnum> capabilities;
	private List<String> requiredParameters;
	private List<String> allowedParameters;
	private List<String> deniedParameters;
	private String minWrappingTTL, maxWrappingTTL;

	public Policy(String path) {
		this();
		this.path = path;
	}

	private Policy() {
	}

	public static class Builder {

		private String path;
		private List<CapabilityEnum> capabilities;
		private List<String> requiredParameters;
		private List<String> allowedParameters;
		private List<String> deniedParameters;
		private String minWrappingTTL, maxWrappingTTL;

		public Policy build() {

			Policy p = new Policy(path);
			p.capabilities = this.capabilities;
			p.requiredParameters = this.requiredParameters;
			p.allowedParameters = this.allowedParameters;
			p.deniedParameters = this.deniedParameters;
			p.minWrappingTTL = this.minWrappingTTL;
			p.maxWrappingTTL = this.maxWrappingTTL;

			return p;
		}

		public Builder withPath(String path) {
			this.path = path;
			return this;
		}

		public Builder withCapabilities(List<CapabilityEnum> list) {
			this.capabilities = list;
			return this;
		}

		public Builder add(CapabilityEnum cap) {
			if (this.capabilities == null)
				capabilities = new ArrayList<CapabilityEnum>();
			this.capabilities.add(cap);
			return this;
		}

		// TODO
	}

	public String toString() {
		JSONObject obj = new JSONObject();

		JSONObject capMap = new JSONObject();
		capMap.put("capabilities", capabilities);

		JSONObject pathMap = new JSONObject();
		pathMap.put(path, capMap);

		obj.put("path", pathMap);

		return obj.toString();
	}

	public String getPath() {
		return path;
	}

	public List<CapabilityEnum> getCapabilities() {
		return capabilities;
	}

	public List<String> getRequiredParameters() {
		return requiredParameters;
	}

	public List<String> getAllowedParameters() {
		return allowedParameters;
	}

	public List<String> getDeniedParameters() {
		return deniedParameters;
	}

	public String getMinWrappingTTL() {
		return minWrappingTTL;
	}

	public String getMaxWrappingTTL() {
		return maxWrappingTTL;
	}

}
