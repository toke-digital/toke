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

TBD - in flux at the moment