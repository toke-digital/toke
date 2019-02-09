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

/**
 * Input parameters for housekeeping (Token lifecycle management)
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class HousekeepingConfig {
	
	// turn on unseal support
	boolean unseal;
	// keys must be in a file, one per line
	List<String> unsealKeys;
	
	// turn on renew support - does not apply to periodic tokens we always try to renew them
	boolean renew; 
	long period; // period to check server, in seconds, default is 300 (every 5 minutes)
	long min_ttl; // the minimum amount of time, in seconds, we are Ok with this token approaching expiry. default is 30 min.
	
	
	// turn on remote host testing
	boolean testReachable;
	boolean pingHost;

	public HousekeepingConfig() {
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
	 * The file must have the keys one per line with no other content
	 * @param keyFile
	 * @return
	 * @throws IOException 
	 */
	public HousekeepingConfig unsealKeys(File keyFile) throws IOException {
		unsealKeys = new ArrayList<String>();
		Files.lines(keyFile.toPath()).forEach(item -> {
			if(item != null && item.trim().length()>0) unsealKeys.add(item);}
		);
		return this;
	}
}
