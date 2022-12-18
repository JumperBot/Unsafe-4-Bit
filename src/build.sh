#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/unsafe-4-bit"
UFB_RELEASE_BIN="../build/unsafe-4-bit"
UFB_MAIN_TEST="../test/UFB/Main.ufb"

cargo build -r
mv -f "$UFB_RELEASE_BIN_TEMP" "$UFB_RELEASE_BIN"
rm "$UFB_RELEASE_BIN_TEMP"
find . -type f -name "*.d" -delete
rmdir "$UFB_RELEASE_DIR/examples"
rmdir "$UFB_RELEASE_DIR/incremental"
