#!/bin/bash
UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/ufb"
UFB_RELEASE_BIN="../build/ufb"
UFB_MAIN_TEST="../test/UFB/Main.ufb"

# https://stackoverflow.com/a/60779604/16915219
\time -f %M "$UFB_RELEASE_BIN" "${UFB_MAIN_TEST}b"
echo "^ Maximum resident set size (kilobytes) ^"

# https://stackoverflow.com/a/16961051/16915219
ts=$(date +%s%N)
"$UFB_RELEASE_BIN" "${UFB_MAIN_TEST}b"
echo "It took $((($(date +%s%N) - $ts)/1000000))ms, yo."
