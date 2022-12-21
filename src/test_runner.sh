#!/bin/bash

UFB_MAIN_TEST="../test/UFB/Main.ufb"
UFB_DEBUG_BIN="target/debug/ufb"

cargo build

# https://stackoverflow.com/a/60779604/16915219
\time -f %M "$UFB_DEBUG_BIN" -pmn "${UFB_MAIN_TEST}b"
echo "^ Maximum resident set size (kilobytes) ^"
