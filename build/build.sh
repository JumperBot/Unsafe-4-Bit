#!/bin/sh
cd UFB
rm *
echo "javac ../../src/UFB/*.java -d . -Xdiags:verbose"
javac ../../src/UFB/*.java -d . -Xdiags:verbose
echo "jar --create --file=../UFB.jar --main-class=UFB *.class"
jar --create --file=../UFB.jar --main-class=UFB *.class
cd ../..
# Copy changed to codespace extensions directory
echo "sudo rm -r ~/.vscode-remote/extensions/unsafe-4-bit"
sudo rm -r ~/.vscode-remote/extensions/unsafe-4-bit
echo "cp -r syntax-highlight/* ~/.vscode-remote/extensions/"
cp -r syntax-highlight/* ~/.vscode-remote/extensions/
cd build