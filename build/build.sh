#!/bin/sh
cd UFB
javac -verbose ../../src/UFB/*.java -d .
echo
jar --create --file=../UFB.jar --verbose --main-class=UFB *.class
cd ..