# toke - A Driver for Hashicorp's Vault 

Toke is a java driver for Hashicorp Vault. Toke aims to implement a managed life-cycle product for working with secrets, tokens, and policies within Vault.

### Origins

I've used Steve Perkin's [BetterCloud driver](https://github.com/BetterCloud/vault-java-driver) 
successfully but I needed KVv2 support, which that project did not provide at the time. They have a 
little different idea on design as well. This is intended to be an enterprise solution and to do lots of
things which go a bit beyond the basics.

Requires
---------------------

Java 8

## The Problem We Are Trying to Solve
---------------------

One typical use case for Hashicorp Vault is at deploy time we just need a few secrets out of the vault, for example in
the process of running Ansible builds. This can be done with curl or with some simple java calls using the java.io.* classes.

But what about the scenario in an enterprise application which may run for days, weeks, or months? In that
scenario we need to be able to update the running application with zero-downtime and renew tokens, change policies,
etc. without necessarily having to involve DevOps or Vault Admins in the process. 

Then there are are testing scenarios we would like to implement which require much more complete control. 


## Current Status
---------------------

At this early stage the driver supports KVv1, KVv2, some of Sys, and other useful API calls with token login. Auto-renewal on tokens is working. Auto-unseal is an
optional feature here rather than a bug, as it is quite useful during testing. 

A lot of regression testing and scenarios yet to be completed. You can help by submitting a pull request! :-)  

## RoadMap/Progress

29/03/2019 - Artifact name change in toke pom, addition of toke-path maven module (for optional json-path support)

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
				.kvName("toke-kv1")   // the driver supports accessing one KV v1 and one KV v2 secrets engine by default, these can be changed dynamically
				.kv2Name("toke-kv2")
				.authType("TOKEN")  // see AuthType enum for the supported types 
				.housekeepingConfig(hc)
				.build();
		 
		TokeDriver driver = new TokeDriver(config);
		driver.isReady(); // block until driver token available or use
```

Once the driver has initialized it automatically attempts to log in with the configured authentication settings. It acquires a token for use with the further operations. For
example, if the driver logs in with the root token, it does not then use the root token, but the resulting token from a login operation.

Once the Vault login has been successful, the acquired token is communicated within the driver using a synchronous Event-driven model. Currently implemented modules:

Sys - for API calls to sys/*
KVv1 - for API calls to a KVv1 secrets engine
KVv2 - for API calls to a KVv2 secrets engine


## Housekeeping

The Housekeeping classes implement a background thread which tests vault is present, unsealed, that we are logged in, checks our the token for validity to 
see if it needs to be renewed, etc. In principle, and based on how the client's authentication has been configured, the driver can keep a token active 
through renewal, or through additional login, for as long as the client application is running.  


##Usage

Using a driver module is straightforward:

TBD

```

// write some secrets in a KV v1 secrets engine
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
	<artifactId>driver</artifactId>
	<version>1.0.1</version>
</dependency>
```

Test suite:

```
<dependency>
   	<groupId>digital.toke</groupId>
	<artifactId>toke-test-suite</artifactId>
	<version>1.0.1</version>
</dependency>

```




