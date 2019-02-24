# toke - A Driver for Hashicorp's Vault 

toke is a java driver for Hashicorp vault. It aims to implement a managed life-cycle product for secrets, tokens, and policies.

### Origins

I've used Steve Perkin's [BetterCloud driver](https://github.com/BetterCloud/vault-java-driver) 
successfully but I needed KVv2 support, which that project doesn't provide. They have a little different
idea on design as well. 

Requires
---------------------

Java 8

## Problem we are trying to solve
---------------------

Typical use of Hashicorp Vault is at deploy time we just need a few secrets out of the vault, for example in
the process of running Ansible builds. 

But what about the scenario in an enterprise application which may run for days, weeks, or months? In that
scenario we need to be able to update the running application with zero-downtime, even during
vault key rotations, etc? Or, what if our application is vault-intensive?

## Current status
---------------------

The driver supports KVv1, KVv2, Sys, and some other useful API calls. Auto-renewal on tokens is working. Auto-unseal is an
optional feature here rather than a bug as it is quite useful during testing.  

A lot of regression testing and scenarios yet to be completed. Help me develop it by sending pull requests! 

## Documentation

TBD

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




