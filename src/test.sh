UFB_RELEASE_DIR="target/release"
UFB_RELEASE_BIN_TEMP="$UFB_RELEASE_DIR/unsafe-4-bit"
UFB_RELEASE_BIN="../build/unsafe-4-bit"
UFB_MAIN_TEST="../test/UFB/Main.ufb"

cargo build -r
mv $UFB_RELEASE_BIN_TEMP $UFB_RELEASE_BIN
sudo find . -type f -name "*.d" -delete
rmdir $UFB_RELEASE_DIR/examples
rmdir $UFB_RELEASE_DIR/incremental
\time -v $UFB_RELEASE_BIN -c $UFB_MAIN_TEST
flamegraph --root --open -- $UFB_RELEASE_BIN -c $UFB_MAIN_TEST
sleep 2
sudo rm perf.data*
sudo rm flamegraph.svg
