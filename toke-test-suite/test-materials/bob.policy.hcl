
# create a policy for bob - for testing

# this policy set works with a kv-v1 secrets engine mounted at "toke-kv1/" and a kv-v2 engine mounted at "toke-kv2/"

# auth

# needed for driver operation
path "auth/token/lookup-self" {
  capabilities = ["read"]
}

# needed for driver operation
path "auth/token/renew-self" {
  capabilities = ["update"]
}

# sys

# needed for driver operation
path "sys/health" {
  capabilities = ["read"]
}

# needed for driver operation
path "sys/capabilities" {
  capabilities = ["read"]
}

# needed for driver operation
path "sys/capabilities-self" {
  capabilities = ["read"]
}

# needed for driver operation
path "sys/capabilities-accessor" {
  capabilities = ["read"]
}

# the exact permissions on this endpoint are ambiguous or undocumented, so give a likely set. 
path "sys/wrapping/*" {
  capabilities = ["read", "create", "update"]
}

# kv1

# can read, create, update, and delete latest version of a key
path "toke-kv1/*" {
  capabilities = ["create", "update", "read", "delete", "list"]
}


# kv2

# can read, create, update, and delete latest version of a key
path "toke-kv2/data/*" {
  capabilities = ["create", "update", "read", "delete", "list"]
}

# can soft-delete any version of the key
path "toke-kv2/delete/*" {
  capabilities = ["update"]
}

# can soft-undelete any key
path "toke-kv2/undelete/*" {
  capabilities = ["update"]
}

# can completely remove 
path "toke-kv2/destroy/*" {
  capabilities = ["update"]
}
# access and also completely destroy metadata and all its secrets
path "toke-kv2/metadata/*" {
  capabilities = ["list", "read", "delete"]
}

