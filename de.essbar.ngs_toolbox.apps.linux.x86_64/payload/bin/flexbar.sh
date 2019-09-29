#!/bin/bash

# Wrapper script to flexbar that accepts the same arguments except
# for the first one which has to be the output file (to be able to output reads in interleaved fromat).

# Get path to output file and shift it.
OUT=$1
shift

# Execute flexbar and output interleaved reads.
echo "flexbar $* -1 >${OUT}"
flexbar $* -1 >"${OUT}"
