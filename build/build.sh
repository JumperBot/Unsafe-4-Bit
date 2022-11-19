#!/bin/sh
cd UFB
echo "javac ../../src/UFB/*.java -d ."
javac ../../src/UFB/*.java -d .
echo "jar --create --file=../UFB.jar --main-class=UFB *.class"
jar --create --file=../UFB.jar --main-class=UFB *.class
cd ..

# Copy build/README.md to src/README.md
echo "cp README.md ../src/README.md"
cp README.md ../src/README.md
