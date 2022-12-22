#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_DIR_MUSL="target/aarch64-unknown-linux-musl/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/ufb"
UFB_RELEASE_BIN_TEMP_MUSL="$UFB_RELEASE_DIR_MUSL/ufb"
UFB_RELEASE_BIN_DIR="../build/aarch64-unknown-linux-gnu"
UFB_RELEASE_BIN_MUSL_DIR="../build/aarch64-unknown-linux-musl"
UFB_RELEASE_BIN="$UFB_RELEASE_BIN_DIR/ufb"
UFB_RELEASE_BIN_MUSL="$UFB_RELEASE_BIN_MUSL_DIR/ufb"

rustfmt src/*
rm $UFB_RELEASE_BIN
rm $UFB_RELEASE_BIN_MUSL
cargo clean
cargo build -r
cargo build -r --target=aarch64-unknown-linux-musl
mv -f "$UFB_RELEASE_BIN_TEMP" "$UFB_RELEASE_BIN"
mv -f "$UFB_RELEASE_BIN_TEMP_MUSL" "$UFB_RELEASE_BIN_MUSL"

UFB_RELEASE_VERSION="$(cat Cargo.toml | grep "version" | cut -d'"' -f 2)"
tar -cf "../build/Unsafe-4-Bit_v"$UFB_RELEASE_VERSION"_aarch64-unknown-linux-gnu.tar" -C $UFB_RELEASE_BIN_DIR .
tar -cf "../build/Unsafe-4-Bit_v"$UFB_RELEASE_VERSION"_aarch64-unknown-linux-musl.tar" -C $UFB_RELEASE_BIN_MUSL_DIR .
sudo rm target -r
