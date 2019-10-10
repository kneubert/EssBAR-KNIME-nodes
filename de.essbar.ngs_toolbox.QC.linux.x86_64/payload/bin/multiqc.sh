#!/bin/bash
# Wrapper script to MultiQC that accepts the same arguments
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export PYTHONPATH="${DIR}/lib/python2.7/site-packages:$PYTHONPATH"

echo "$DIR/multiqc $*"
$DIR/multiqc $*
