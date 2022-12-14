UFB_RELEASE_BIN="target/release/unsafe-4-bit"
UFB_MAIN_TEST="../../test/UFB/Main.ufb"

cargo build -r && \time -v $UFB_RELEASE_BIN -c $UFB_MAIN_TEST
flamegraph --root --open -- $UFB_RELEASE_BIN -c $UFB_MAIN_TEST
sleep 2
rm perf.data*
rm flamegraph.svg
