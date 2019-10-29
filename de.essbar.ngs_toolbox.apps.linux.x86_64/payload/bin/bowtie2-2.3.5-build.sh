#!/bin/bash
# Wrapper script to Bowtie2-2.3.5 that accepts the same arguments
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIR=$DIR/bowtie2-2.3.5

echo "$DIR/bowtie2-build $*"
$DIR/bowtie2-build $*