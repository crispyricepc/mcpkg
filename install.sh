#!/bin/sh

MCPKG_VERSION=2.0-dev
BIN_FILE=$HOME/.local/bin/mcpkg
LIB_FILE=$HOME/.local/lib/mcpkg/mcpkg-$MCPKG_VERSION.jar

mkdir -p $HOME/.local/lib/mcpkg

echo "Running build..."

mvn -B compile assembly:single

echo "Installing mcpkg to $LIB_FILE and $BIN_FILE..."

echo -e "#!/bin/sh\n\
java -jar $LIB_FILE \"\$@\"\n" > $BIN_FILE
chmod +x $BIN_FILE
cp target/mcpkg-$MCPKG_VERSION-jar-with-dependencies.jar $LIB_FILE

echo "Done"
