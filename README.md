# toke - A Driver for Hashicorp's Vault 

toke is a java driver for Hashicorp vault. It aims to implement a managed life-cycle product for secrets, tokens, and policies.

### Origins

I've used Steve Perkin's [BetterCloud driver](https://github.com/BetterCloud/vault-java-driver) 
successfully but I needed KVv2 support, which that project did not provide at the time. They have a 
little different idea on design as well. This is intended to be an enterprise solution. 

Requires
---------------------

Java 8

## Problem we are trying to solve
---------------------

One typical use case for Hashicorp Vault is at deploy time we just need a few secrets out of the vault, for example in
the process of running Ansible builds. 

But what about the scenario in an enterprise application which may run for days, weeks, or months? In that
scenario we need to be able to update the running application with zero-downtime, renew tokens, change policies,
etc. without having to involve DevOps or Vault Admins. Such resources can be quite scarce. 

## Current status
---------------------

The driver supports KVv1, KVv2, Sys, and some other useful API calls with token login. Auto-renewal on tokens is working. Auto-unseal is an
optional feature here rather than a bug here as it is quite useful during testing. 

A lot of regression testing and scenarios yet to be completed. You can help by submitting a pull request.  

## RoadMap

10/03/2019 - I am implementing userpass auth backend API methods, will start soon on LDAP. Maven pom version 1.0.1 will have those, plus the improved test suite.   

## Documentation

#### Basic Use

```
   // used by the TokenManager
	HousekeepingConfig hc = HousekeepingConfig.builder()
	         .reachable(true)   // test networking
				.pingHost(true)    // pingtest networking
				.init(true)        //  request a vault instance be initialized (useful for testing/vault app development scenarios)
				.unseal(true)      // unseal the vault if it is sealed. Useful for test scenarios
				.unsealKeys(keyFile) // if unseal is true, keys and root token will be written here
				.build();
	// The Driver configuration proper
	TokeDriverConfig config = TokeDriverConfig.builder()
				.proto("http")
				.host("127.0.0.1")
				.port(8201)
				.kvName("toke-kv1")   // the driver supports accessing one KV v1 and one KV v2 secrets engine, these can be changed dynamically
				.kv2Name("toke-kv2")
				.authType("TOKEN")  // see AuthType enum for the supported types 
				.housekeepingConfig(hc)
				.build();
		 
		TokeDriver driver = new TokeDriver(config);
		driver.isReady(); // block until driver token available
```

Once the driver has initialized it automatically attempts to log in with the configured settings. It acquires a token for use with the further operations. For
example, if the driver logs in with the root token, it does not then use the root token, but the resulting token from the login operation.

Once the login has been successful, the token is communicated to different modules in the driver: 

Sys - for API calls to sys/*
KVv1 - for API calls to a KVv1 secrets engine
KVv2 - for API calls to a KVv2 secrets engine

Using a module is straightforward:

TBD

```

// write some secrets
KVv1 secretsEngine = driver.kv();
Map<String,Object> map = new HashMap<String,Object>();
map.put("key1", "value1");
map.put("key2", "value2");
secretsEngine.kvWrite("my-secret/subpath", map);


// Write a policy file
Sys sys = driver.sys();
sys.writePolicy("bob", new File("./test-materials/bob.policy.hcl"));



```

Getting it
---------------------

Maven:
```
<dependency>
   	<groupId>digital.toke</groupId>
	<artifactId>vault-java-driver</artifactId>
	<version>1.0.0</version>
</dependency>
```




