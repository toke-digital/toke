# can read, creat, update, and delete latest version of a key
path "toke-kv2/data/*" {
  capabilities = ["create", "update", "read", "delete"]
}

# can soft-delete any version of the key
path "toke-kv2/delete/*" {
  capabilities = ["update"]
}

# can soft-undelete any key
path "secret/undelete/*" {
  capabilities = ["update"]
}

# can completely remove 
path "secret/destroy/*" {
  capabilities = ["update"]
}
# access and also completely destroy metadata and all its secrets
path "secret/metadata/*" {
  capabilities = ["list", "read", "delete"]
}

