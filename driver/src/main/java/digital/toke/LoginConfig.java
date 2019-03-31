package digital.toke;

import java.io.File;
import java.util.Objects;


/**
 * <p>Parameters associated with an individual login. An instance of this class will be associated with 
 * our Token instance for purposes of renewals or re-login of that token.</p>
 * 
 * <p>Note about token logins - the token designated here (such as with the root token) will never be used
 * for method API calls over the wire. Instead, it is only used to login with the TOKEN AuthType and then 
 * the resulting child token from that process is used for API calls.</p>
 * 
 * 
 * @author daves
 *
 */
public class LoginConfig {
	
	// the type of authentication associated with this login attempt
	
	AuthType authType; // e.g., supports TOKEN, LDAP, APPROLE, USERPASS;

	// Used with TOKEN login type, e.g., a root token
	String token;
	
	// indicates the location of the token if it is in a file. "token" above should be empty in that case
	File tokenFile;

	// Used with AppRole type
	String secretId;
	String roleId;

	// used with LDAP and USERPASS auth types
	String username;
	String password;
	

	public LoginConfig(AuthType type) {
		this.authType = type;
	}
	
	public static Builder builder(AuthType type) {
		return new Builder(type);
	}

	public static class Builder {

		AuthType authType; // e.g., supports TOKEN, LDAP, APPROLE, USERPASS;

		// Used with TOKEN login type, e.g., a root token
		String token;
		
		// indicates the location of the token if it is in a file. "token" above should be empty in that case
		File tokenFile;

		// Used with AppRole type
		String secretId;
		String roleId;

		// used with LDAP and USERPASS auth types
		String username;
		String password;
		
		public Builder(AuthType type) {
			this.authType = type;
		}

		public Builder token(String val) {
			token = val;
			return this;
		}
		
		public Builder tokenFile(File val) {
			tokenFile = val;
			return this;
		}
		
		public Builder secretId(String val) {
			secretId = val;
			return this;
		}

		public Builder roleId(String val) {
			roleId = val;
			return this;
		}

		public Builder username(String val) {
			username = val;
			return this;
		}

		public Builder password(String val) {
			password = val;
			return this;
		}
		
		public LoginConfig build() {
			
			LoginConfig config = new LoginConfig(authType);
		
			config.authType = this.authType;
			config.token = this.token;
			config.tokenFile = this.tokenFile;
			config.secretId = this.secretId;
			config.roleId = this.roleId;
			config.username = this.username;
			config.password = this.password;
			

			return config;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(authType, password, roleId, secretId, token, tokenFile, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LoginConfig)) {
			return false;
		}
		LoginConfig other = (LoginConfig) obj;
		return authType == other.authType && Objects.equals(password, other.password)
				&& Objects.equals(roleId, other.roleId) && Objects.equals(secretId, other.secretId)
				&& Objects.equals(token, other.token) && Objects.equals(tokenFile, other.tokenFile)
				&& Objects.equals(username, other.username);
	}

	/**
	 * Used in a special cases, which is vault init
	 * @see HousekeepingBase
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "LoginConfig [authType=" + authType + "]";
	}

}
