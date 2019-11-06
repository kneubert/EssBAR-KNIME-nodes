#!/bin/bash
# Wrapper script for kraken2
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
kraken2_bin=$DIR/kraken2-2.0.8-beta

echo "$kraken2_bin/kraken2 $*"
$kraken2_bin/kraken2 $*