#!/bin/bash

# Wrapper script to mlst that accepts the same arguments except
# for the database (uses the folder).
# first, parse the options:

blastfile=""
datadir=""
# Get path to output file and shift it.
OUT=$1
shift

optspec=":hv-:"
while getopts $optspec optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                datadir)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    		echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
		    		datadir=${val}; 
                    ;;
                blastdb)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    		echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
		    		blastfile=${val}"/mlst.fa"; 
                    ;;
                *)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
                    
            esac;;
        *)
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

shift $((OPTIND - 2))

if [[ ! -e $blastfile ]];
then
   echo ERROR blast file $blastfile does not exist 1>&2
   exit 1
fi

if [[ ! -e $datadir ]];
then
   echo ERROR mlst database directory $datatdir does not exist 1>&2
   exit 1
fi

echo $blastfile

# THEN, access the positional params
echo "there are $# positional params remaining"
for ((i=1; i<=$#; i++)); do
  printf "%d\t%s\n" $i "${!i}"
done
echo "mlst --blastdb $blastfile --datadir $datadir $* >$OUT"
mlst --blastdb $blastfile --datadir $datadir $* >$OUT
