/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 David R. Smith All Rights Reserved 
 */
package digital.toke.spec;

/**
 * Defined engines
 * 
 * @author daves
 *
 */
public enum SecretsEngineType {
	
	
	KV("kv"), KVv2("kv-v2"); 
	
	private final String type;  // this is the 'type' token as understood in the vault documentation, such as kv or aws

	SecretsEngineType(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
