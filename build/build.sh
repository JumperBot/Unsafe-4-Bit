#!/bin/sh
cd UFB
echo "javac ../../src/UFB/*.java -d . -Xdiags:verbose"
javac ../../src/UFB/*.java -d . -Xdiags:verbose
echo "jar --create --file=../UFB.jar --main-class=UFB *.class"
jar --create --file=../UFB.jar --main-class=UFB *.class
cd ..