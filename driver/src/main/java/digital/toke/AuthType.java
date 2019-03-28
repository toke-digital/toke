/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke;

/**
 * These are the currently implemented login (auth) types available
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public enum AuthType {
	TOKEN,LDAP,APPROLE,USERPASS;
}
