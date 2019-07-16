#!/bin/bash

# Wrapper script to srst2 that accepts the same arguments except
# for the input files.
# first, parse the options:
flag="--input_se"
reads=""
optspec=":hv-:"
while getopts $optspec optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                fwd)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
		    reads=${reads}" ${val}"; 
                    ;;
                rev)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
		    reads=${reads}" ${val}"; 
		    paired=1
		    flag="--input_pe"
                    ;;
            esac;;
        *)
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

# and shift them away
shift $((OPTIND - 2))

if [ -n "$paired" ]
then
   echo "paired-end reads input"
   #shift $((OPTIND - 1))
fi



# THEN, access the positional params
echo "there are $# positional params remaining"
for ((i=1; i<=$#; i++)); do
  printf "%d\t%s\n" $i "${!i}"
done
echo "srst2 $* ${flag} ${reads}"
srst2 $* ${flag} ${reads}