#!/bin/bash
# Wrapper script to Prokka that accepts the same arguments
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIR=$DIR/prokka

echo "$DIR/bin/prokka $*"
$DIR/bin/prokka $*
