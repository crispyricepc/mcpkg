#!/bin/sh

MCPKG_VERSION="2.0-dev"

mkdir -p dist/usr/lib/mcpkg
mkdir -p dist/usr/bin/

BIN_DIR=$DIST_DIR/usr/bin
BIN_FILE=$BIN_DIR/mcpkg
LIB_DIR=$DIST_DIR/usr/lib/mcpkg
LIB_FILE=$LIB_DIR/mcpkg-$MCPKG_VERSION-jar-with-dependencies.jar

echo "Running build..."

mvn compile assembly:single

echo "Installing mcpkg to $LIB_FILE and $BIN_FILE..."

printf "#!/usr/bin/env sh\n\
java -jar $LIB_FILE \"\$@\"\n" > $BIN_FILE
chmod +x $BIN_FILE
cp target/mcpkg-$MCPKG_VERSION-jar-with-dependencies.jar $LIB_DIR

echo "Done"
