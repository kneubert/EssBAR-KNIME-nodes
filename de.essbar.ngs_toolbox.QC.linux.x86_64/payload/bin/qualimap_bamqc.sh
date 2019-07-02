#!/bin/bash

# Wrapper script for qualimap that accepts the same arguments
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
qDIR=${DIR}/qualimap_v2.2.1

cov_file=$1
shift

dir=`dirname $cov_file`

if [ ! -z "$dir" ]; then
   echo mkdir -p $dir
   mkdir -p $dir
fi

echo "$qDIR/qualimap bamqc -oc $cov_file $*"
$qDIR/qualimap bamqc -oc $cov_file $*

