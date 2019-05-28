#!/bin/bash
# Wrapper script to QUAST that accepts the same arguments
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export PYTHONPATH=${DIR}/quast_lib:${PYTHONPATH}

echo "$DIR/quast.py $*"
$DIR/quast.py $*
