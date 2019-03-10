package digital.toke.auth;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import digital.toke.Networking;
import digital.toke.ServiceBase;
import digital.toke.TokeDriverConfig;
import digital.toke.Token;
import digital.toke.accessor.Toke;
import digital.toke.event.EventEnum;
import digital.toke.event.TokenEvent;
import digital.toke.event.TokenListener;
import digital.toke.exception.ConfigureException;
import digital.toke.exception.ReadException;

/**
 * "userpass" API methods
 * 
 * @author daves
 *
 */
public class UserPass extends ServiceBase implements TokenListener {
	
	private static final Logger logger = LogManager.getLogger(UserPass.class);

	protected TokeDriverConfig config;
	protected Token token;
	protected Networking client;

	public UserPass(TokeDriverConfig config, Networking client) {
		super();
		this.config = config;
		this.client = client;
		logger.info("Initialized UserPass instance");
	}

	@Override
	public void tokenEvent(TokenEvent evt) {
		if (evt.getType().equals(EventEnum.LOGIN)) {
			token = evt.getToken();
			countDown();
			logger.info("Token with accessor " + token.accessor() + " set on Sys");
		}

		if (evt.getType().equals(EventEnum.RELOAD_TOKEN)) {
			token = evt.getToken();
			// countDown();
			logger.info("Reloaded token on a UserPass instance.");
		}
	}
	
	public Toke createUpdateUser(UserSpec user) throws ConfigureException {
		String url = config.baseURL().append("/auth/"+user.authPath+"/users/"+user.username).toString();
		logger.debug("Using: " + url);
		
		try {
			Toke response = client.put(url, user.toString(), true);
			// we expect a 204 per the documentation
			if(response.code != 204) throw new ConfigureException("Failed to get a 204 response on "+"/auth/"+user.authPath+"/users/"+user.username);
			return response;
		} catch (IOException e) {
			throw new ConfigureException(e);
		}
	}
	
	/**
	 * Normal case
	 * 
	 * @param username
	 * @return
	 * @throws ReadException
	 */
	public Toke readUser(String username) throws ReadException {
		return readUser(username,"userpass");
	}
	
	/**
	 * Userpass was enabled on a custom path, such as "my-userpass"
	 * 
	 * @param username
	 * @param authPath
	 * @return
	 * @throws ReadException
	 */
	public Toke readUser(String username, String authPath) throws ReadException {
		String url = config.baseURL().append("/auth/"+authPath+"/users/"+username).toString();
		logger.debug("Using: " + url);
		try {
			Toke response = client.get(url);
			// we expect a 200 per the documentation
			if(response.code != 200) throw new ReadException("Failed to get a 200 response on "+"/auth/"+authPath+"/users/"+username);
			return response;
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}

}
