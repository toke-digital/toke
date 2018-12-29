/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

/**
 * These are the currently implemented login (auth) types available
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public enum AuthType {
	TOKEN,LDAP,APPROLE,USERPASS;
}
