/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Input parameters for housekeeping (Token lifecycle management)
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class HousekeepingConfig {
	
	private static final Logger logger = LogManager.getLogger(HousekeepingConfig.class);
	
	// turn on init support
	boolean init; 
	
	// turn on unseal support
	boolean unseal;
	
	// keys will be in a file one per line external to the application
	List<String> unsealKeys;
	File keyFile;
	
	// turn on renew support - does not apply to periodic tokens, we always try to renew those
	boolean renew; 
	long period; // period to check server, in seconds, default is 300 (every 5 minutes)
	long min_ttl; // the minimum amount of time, in seconds, we are Ok with this token approaching expiry. default is 30 min.
	
	
	// turn on remote host testing features
	boolean testReachable;
	boolean pingHost;

	/**
	 * Create an instance with some sane defaults.
	 */
	public HousekeepingConfig() {
		init = false;
		unseal = false;
		renew = true;
		period = 300; // check every 5 min
		min_ttl = 1800; // renew if difference between expire_date and now is less than 30 min.  
		
		testReachable = true;
		pingHost = true;
		
	}
	
	public static HousekeepingConfig defaultInstance() {
		return new HousekeepingConfig();
	}

	List<String> getUnsealKeys() {
		return unsealKeys;
	}
	
	public HousekeepingConfig init(boolean attemptToInit) {
		init = attemptToInit;
		return this;
	}
	
	public HousekeepingConfig unseal(boolean attemptToUnseal) {
		unseal = attemptToUnseal;
		return this;
	}
	
	public HousekeepingConfig renew(boolean attemptToRenewTokens) {
		renew = attemptToRenewTokens;
		return this;
	}
	
	public HousekeepingConfig period(int periodInSeconds) {
		period = periodInSeconds;
		return this;
	}
	
	public HousekeepingConfig minttl(int minInSeconds) {
		min_ttl = minInSeconds;
		return this;
	}
	
	public HousekeepingConfig pingHost(boolean pingHost) {
		this.pingHost = pingHost;
		return this;
	}
	
	public HousekeepingConfig reachable(boolean testReachable) {
		this.testReachable = testReachable;
		return this;
	}
	
	public HousekeepingConfig unsealKeys(List<String> keys) {
		this.unsealKeys = keys;
		return this;
	}
	
	/**
	 * The file must have the keys one per line with no other content.
	 * 
	 * @param keyFile
	 * @return
	 * @throws IOException 
	 */
	public HousekeepingConfig unsealKeys(File keyFile) {
	
		unsealKeys = new ArrayList<String>();
		this.keyFile = keyFile;
		
		// cannot do this until keys are created if init = true
		if(init == false) {
			try {
				Files.lines(keyFile.toPath()).forEach(item -> {
					if(item != null && item.trim().length()>0) unsealKeys.add(item);
			     }
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return this;
	}
	
	public HousekeepingConfig build() {
		
		// complete any builder-style work here, prior to using the config.
		logger.debug(this.toString());
		
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (init ? 1231 : 1237);
		result = prime * result + ((keyFile == null) ? 0 : keyFile.hashCode());
		result = prime * result + (int) (min_ttl ^ (min_ttl >>> 32));
		result = prime * result + (int) (period ^ (period >>> 32));
		result = prime * result + (pingHost ? 1231 : 1237);
		result = prime * result + (renew ? 1231 : 1237);
		result = prime * result + (testReachable ? 1231 : 1237);
		result = prime * result + (unseal ? 1231 : 1237);
		result = prime * result + ((unsealKeys == null) ? 0 : unsealKeys.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HousekeepingConfig other = (HousekeepingConfig) obj;
		if (init != other.init)
			return false;
		if (keyFile == null) {
			if (other.keyFile != null)
				return false;
		} else if (!keyFile.equals(other.keyFile))
			return false;
		if (min_ttl != other.min_ttl)
			return false;
		if (period != other.period)
			return false;
		if (pingHost != other.pingHost)
			return false;
		if (renew != other.renew)
			return false;
		if (testReachable != other.testReachable)
			return false;
		if (unseal != other.unseal)
			return false;
		if (unsealKeys == null) {
			if (other.unsealKeys != null)
				return false;
		} else if (!unsealKeys.equals(other.unsealKeys))
			return false;
		return true;
	}
	
}
