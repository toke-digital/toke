package digital.toke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import digital.toke.exception.LoginFailedException;

/**
 * The auth module implements a vault login using various auth types such as LDAP and APPROLE. 
 * Tokens are sent wrapped in events to interested parties
 * 
 * See https://www.vaultproject.io/docs/auth/
 * 
 * @author Dave
 *
 */
public class Auth {

	private static final Logger logger = LogManager.getLogger(Auth.class);
	
	DriverConfig config;
	Networking client;
	
	private List<TokenListener> listeners;
	
	public Auth(DriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		listeners = new ArrayList<TokenListener>();
		logger.info("Auth instance "+this.hashCode()+" configured");
	}
	
	
	public void logoff(Token token) {
		// destroy token
	}
	
	public void logoff() {
		// destroy token
	}
	
	// Logins. All logins are POSTs
	
	protected void loginLDAP() throws LoginFailedException {
		String url = config.authLdapLogin();
		JSONObject json = new JSONObject();
		json.put("password", config.password);
		Token toke = httpLogin(url,json);
		this.fireLoginEvent(toke);
	}
	
    protected void loginAppRole() throws LoginFailedException {
		String url = config.authAppRoleLogin();
		JSONObject json = new JSONObject();
		json.put("role_id", config.roleId);
		json.put("secret_id", config.secretId);
		Token toke = httpLogin(url,json);
		this.fireLoginEvent(toke);
	}
    
    protected void loginUserPass() throws LoginFailedException {
  		String url = config.authUserPassLogin();
  		JSONObject json = new JSONObject();
  		json.put("password", config.password);
  		Token toke = httpLogin(url,json);
		this.fireLoginEvent(toke);
  	}
    
    protected void loginToken() throws LoginFailedException {
  		String url = config.authTokenLogin();
  		JSONObject json = new JSONObject();
  		// TODO at the moment only supporting one config property here
  		json.put("renewable", config.renewable);
  		Toke result = null;
    	try {
  			result = client.loginToken(url, json.toString(), config.token);
  		} catch (IOException e) {
  			throw new LoginFailedException(e);
  		}
    	Token toke = new Token(new JSONObject(result.response), result.successful);
		this.fireLoginEvent(toke);
    }
    
    private Token httpLogin(String url, JSONObject json) throws LoginFailedException {
    	
    	Toke result = null;
    	try {
  			result = client.login(url, json.toString());
  		} catch (IOException e) {
  			throw new LoginFailedException(e);
  		}
    	return new Token(new JSONObject(result.response), result.successful);
    }
    
    private void fireLoginEvent(Token toke) {
    	
    	if(toke.isFromSuccessfulLoginRequest()) {
		    fireTokenEvent(new TokenEvent(this, toke, EventEnum.LOGIN));
		    logger.info("Fired successful login...");
		}else {
			fireTokenEvent(new TokenEvent(this, toke, EventEnum.FAILED_LOGIN));
			  logger.info("Unsuccessful login attempt...");
			  logger.debug(toke.getJson().toString());
		}
    }

	
	public void login() throws LoginFailedException {
		
		switch(config.authType) {
			case LDAP: {
				loginLDAP();
				break;
			}
			case APPROLE: {
				loginAppRole();
				break;
			}
			case USERPASS: {
				loginUserPass();
				break;
			}
			case TOKEN: {
				loginToken();
				break;
			}
			default: {
				// should fail before this
				break;
			}
		}
	}
	
	public void addTokenListener(TokenListener listener) {
		listeners.add(listener);
	}
	
	protected void fireTokenEvent(TokenEvent evt) {
		for(TokenListener l: listeners) {
			l.tokenEvent(evt);
		}
	}
}
