#!/bin/bash

# Wrapper script for spades that accepts the same arguments
OUT=$1
shift

echo Parameter: $*

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#if [ ! -z "$dir" ]; then
# echo mkdir -p $dir
# mkdir -p $dir
#fi

output_dir=`dirname $OUT`
echo "spades.py -o $output_dir $*"
spades.py -o $output_dir $*
