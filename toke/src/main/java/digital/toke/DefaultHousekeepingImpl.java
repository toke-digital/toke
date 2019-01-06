/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Basic housekeeping (token lifecycle management).
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 * @see HousekeepingConfig
 * @see HousekeepingBase
 */
public class DefaultHousekeepingImpl extends HousekeepingBase {
	
	private static final Logger logger = LogManager.getLogger(DefaultHousekeepingImpl.class);


	public DefaultHousekeepingImpl(TokenManager parent) {
		super(parent);
	}

	@Override
	public void run() {
		
		logger.debug("Starting housekeeping run...");
		
		if(this.config.testReachable) {
			if(!tokenManager.getAuth().hostIsReachable()) {
				logger.error("Host not reachable...bailing out of housekeeping.");
				return;
			}
		}
		
		if(this.config.pingHost) {
			if(!tokenManager.getAuth().pingHost()) {
				logger.error("Socket probe failed...bailing out of housekeeping.");
				return;
			}
		}
		
		// 1.0 - see if unseal requested or needed to get our vault back up and running
		unseal();
		
		// 1.1 - check for cached tokens, if we have some, validate and load them
		loadCachedTokens();
		
		// 1.2 - if a login is required to get a new token, do that
		login();
		
		// 1.3 - this is needed because updates to the set must be synchronized
		List<TokenRenewal> renewals = renew();
		tokenManager.updateManagedSet(renewals); // this also fires event, sends list
		
		logger.debug("Completed housekeeping run...");
		
	}
	
	
}
