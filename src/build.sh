#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/ufb"
UFB_RELEASE_BIN="../build/ufb"
UFB_MAIN_TEST="../test/UFB/Main.ufb"

cargo clean
cargo build -r
mv -f "$UFB_RELEASE_BIN_TEMP" "$UFB_RELEASE_BIN"
sudo rm target -r
#rm "$UFB_RELEASE_BIN_TEMP"
#find . -type f -name "*.d" -delete
#find . -type d -empty -delete
#rmdir "$UFB_RELEASE_DIR/examples"
#rmdir "$UFB_RELEASE_DIR/incremental"
