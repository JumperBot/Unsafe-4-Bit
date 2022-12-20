#!/bin/bash

UFB_MAIN_TEST="../test/UFB/Main.ufb"
UFB_DEBUG_BIN="target/debug/ufb"

cargo build

# https://stackoverflow.com/a/60779604/16915219
\time -f %M "$UFB_DEBUG_BIN" -c "$UFB_MAIN_TEST"
echo "^ Maximum resident set size (kilobytes) ^"

# https://stackoverflow.com/a/16961051/16915219
ts=$(date +%s%N)
"$UFB_DEBUG_BIN" -c "$UFB_MAIN_TEST"
echo "It took $((($(date +%s%N) - $ts)/1000000))ms, yo."
