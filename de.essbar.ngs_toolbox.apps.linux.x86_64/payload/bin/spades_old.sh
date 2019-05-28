#!/bin/bash

# Wrapper script to spades that accepts the same arguments except
# for the first one which has to be the output file (to be able to output reads in interleaved fromat).

# Execute flexbar and output interleaved reads.
echo "spades.py $*"
spades.py $*
