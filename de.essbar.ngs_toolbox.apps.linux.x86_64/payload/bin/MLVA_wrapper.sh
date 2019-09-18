#!/bin/bash
# Wrapper script to MLST_finder.py that accepts the same arguments except for the output files

MLVA_file=$1
Sequence_file=$2
echo $MLVA_file
echo $Sequence_file
shift
shift
output_dir=`dirname $MLVA_file`

echo "python3 MLVA_finder.py $* -o $output_dir"
python3 MLVA_finder.py $* -o $output_dir

MLVA_output=$(echo "${output_dir}/MLVA_analysis*.csv")
Sequence_output=$(echo "${output_dir}/*_output.csv")

echo "mv $MLVA_output $MLVA_file"
mv $MLVA_output $MLVA_file

echo "mv $Sequence_output $Sequence_file"
mv $Sequence_output $Sequence_file

