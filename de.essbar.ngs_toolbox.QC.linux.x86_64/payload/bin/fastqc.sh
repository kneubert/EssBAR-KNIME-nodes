#!/bin/bash

# Wrapper script for fastqc that accepts the same arguments
# Get path to input file and shift it.
INPUT_FILE=$1
shift

echo Parameter: $*

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# create the temporary output directory (fastqc does not create it)
dir=""
while getopts o: opt 2>/dev/null
do
   case $opt in
       o) dir=$OPTARG;;
      # ?) echo "($0): Ein Fehler bei der Optionsangabe"
   esac
done

if [ ! -z "$dir" ]; then
 echo mkdir -p $dir
 mkdir -p $dir
fi

echo $DIR/FastQC/fastqc $* $INPUT_FILE
$DIR/FastQC/fastqc $* $INPUT_FILE
