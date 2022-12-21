#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/ufb"
UFB_RELEASE_BIN="../build/aarch64-unknown-linux-gnu/ufb"
UFB_MAIN_TEST="../test/UFB/Main.ufb"

rustfmt src/*
cargo clean
cargo build -r
mv -f "$UFB_RELEASE_BIN_TEMP" "$UFB_RELEASE_BIN"
sudo rm target -r
