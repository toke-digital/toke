package digital.toke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Basic housekeeping (token lifecycle management).
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class Housekeeping extends HousekeepingBase {
	
	private static final Logger logger = LogManager.getLogger(Housekeeping.class);


	public Housekeeping(TokenManager parent) {
		super(parent);
	}

	@Override
	public void run() {
		
		logger.debug("Starting housekeeping run...");
		
		// 1.0 - see if unseal requested or needed to get our vault back up and running
		unseal();
		
		// 1.1 - check for cached tokens, if we have some, validate and load them
		loadCachedTokens();
		
		// 1.2 - if a login is required to get a new token, do that
		login();
		
		logger.debug("Completed housekeeping run...");
		
	}
}
