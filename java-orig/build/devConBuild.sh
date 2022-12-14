#!/bin/sh
cd ..
# Copy changed to codespace extensions directory
echo "cp -r syntax-highlight/ ~/.vscode-remote/extensions/"
cp -r syntax-highlight/ ~/.vscode-remote/extensions/
cd build