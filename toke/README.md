Toke
=================
A Driver for the [Vault](https://www.vaultproject.io/) secrets management solution from HashiCorp.

This package includes a partial implementation of the Vault API - basically the bits I need for
my project and things that interest me. 

I've used Steve Perkin's [BetterCloud driver](https://github.com/BetterCloud/vault-java-driver) 
successfully for a while but I need KVv2 support, which that project doesn't provide. I looked at 
the possibility of forking their code but I guess I just have some different ideas about design.

Requires
---------------------

Java 8


Getting it
---------------------

Will be on Maven Central quite soon...

Maven:
```
<dependency>
   	<groupId>digital.toke</groupId>
	<artifactId>vault-java-driver</artifactId>
	<version>1.0.0</version>
</dependency>
```

Usage and Abusage
---------------------

Like BetterCloud, Toke offers a fluent API for configuration. 

```
	  Driver driver;
	  String username = ..., password = ...;
	  
      DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.authType("LDAP")
				.username(username)
				.password(password);
		
		driver = new Driver(config);
		
		try {
		    driver.auth().login();
		}catch(LoginFailedException x) {
			x.printStackTrace();
			Assertions.fail(x);
		}
		
		Toke response = null;
		try {
		
			response = driver.kv().kvWrite("test/stuff", new JSONObject().put("key0", "value0").put("key1", 100));
			Assertions.assertEquals(204, response.code);// successful write
			
			response = driver.kv().kvRead("test/stuff");
			Assertions.assertTrue(response.data().map().containsKey("key0"));
			
			response = driver.kv().kvList("test/");
			Assertions.assertEquals(1, response.kvList().secrets().size()); //list secrets, in this case the one we wrote
			
			response = driver.kv().kvDelete("test/stuff");
			Assertions.assertEquals(204, response.code);// successful delete
			
		} catch (TokeException e) {
			 e.printStackTrace();
			 Assertions.fail(e);
		}
		
```


TBD