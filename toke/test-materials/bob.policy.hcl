
# create a policy for bob - for testing

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

path "sys/wrapping/*" {
  capabilities = ["read"]
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

