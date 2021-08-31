#!/usr/bin/env sh

mkdir -p checksums

sha1sum mcpkg*.jar > checksums/sha1sum.txt
sha256sum mcpkg*.jar > checksums/sha256sum.txt
sha512sum mcpkg*.jar > checksums/sha512sum.txt