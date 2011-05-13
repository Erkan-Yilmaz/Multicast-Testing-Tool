#!/bin/sh

# in order to create a package named [name] call
# ./createdeb.sh [name]

TARGET=all

mkdir -p $TARGET/usr/share/icons
mkdir -p $TARGET/usr/share/applications
mkdir -p $TARGET/usr/bin
mkdir -p $TARGET/opt/mctool
mkdir -p $TARGET/DEBIAN

cp ../src/main/resources/images/logo.png $TARGET/usr/share/icons/mctool_icon.png
cp ../target/mctool-*-jar-with-dependencies.jar $TARGET/opt/mctool/mctool.jar
cp control $TARGET/DEBIAN
cp mctool.desktop $TARGET/usr/share/applications

echo -e "#!/bin/sh\njava -jar /opt/mctool/mctool.jar" > $TARGET/usr/bin/mctool
chmod +x $TARGET/usr/bin/mctool

dpkg -b $TARGET $1
