#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/ufb"
UFB_RELEASE_BIN_DIR="../build/aarch64-unknown-linux-gnu"
UFB_RELEASE_BIN="$UFB_RELEASE_BIN_DIR/ufb"
RUSTFLAGS="--remap-path-prefix $HOME=~"

rustfmt src/*
rm $UFB_RELEASE_BIN_DIR/*
rm ../build/*.tar
cargo clean

cargo build -r
mv -f "$UFB_RELEASE_BIN_TEMP" "$UFB_RELEASE_BIN"

UFB_RELEASE_VERSION="$(cat Cargo.toml | grep -m 1 "version" | cut -d'"' -f 2)"
tar -cf "../build/Unsafe-4-Bit_v"$UFB_RELEASE_VERSION"_aarch64-unknown-linux-gnu.tar" -C $UFB_RELEASE_BIN_DIR . --numeric-owner
sudo rm target -r

rm Cargo.lock
