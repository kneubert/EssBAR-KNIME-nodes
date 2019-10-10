#!/bin/bash

# Wrapper script to kraken-report that accepts the same arguments except
# for the first one which has to be the output file.

# Get path to output file and shift it.
OUT=$1
shift

# Execute kraken-report and redirect stdout to the output file
echo "kraken-report $* >$OUT"
kraken-report $* >"$OUT"
