#!/bin/bash

# Wrapper script to mlst that accepts the same arguments except for the blast database 
# (overwrite parameter to use the mlst.fa file for the blast db inside the given folder)


datadir=""
blastfile=""
blastval=""
novelfile="novel.fa"

# Get path to output file and shift it.
OUT=$1
shift

optspec=":hv-:"
while getopts $optspec optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
				mlst_db)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    		echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
					datadir=${val}
                    ;;	
                blast_db)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    		echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
					blastval=${val}
		    		blastfile="${val}/mlst.fa"; 
                    ;;		
				novel)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    		echo "Parsing option: '--${OPTARG}', value: '${val}'" >&2;
					novelfile=${val}
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

echo $blastval
# remove --blastdb parameter and value
for arg do 
   shift
   [ "$arg" = "--blast_db" ] || [ "$arg" = "${blastval}" ] || [ "$arg" = "--mlst_db" ] || [ "$arg" = "${datadir}" ] && continue
   set -- "$@" "$arg"
done


# check if fasta file mlst.fa for blast db exists
if [[ ! -e $blastfile ]];
then
   echo ERROR blast file $blastfile does not exist 1>&2
   exit 1
fi

# check if folder for mlst database exists
if [[ ! -e $datadir ]];
then
   echo ERROR mlst database directory $datadir does not exist 1>&2
   exit 1
fi

# THEN, access the positional params
echo "there are $# positional params remaining"
for ((i=1; i<=$#; i++)); do
  printf "%d\t%s\n" $i "${!i}"
done

echo "mlst --blastdb $blastfile --datadir $datadir $* >$OUT"
mlst --blastdb $blastfile --datadir $datadir $* >$OUT

# create empty file for novel alleles
if [[ ! -e $novelfile ]];
then 
  echo "Found no novel intact alleles." >> $novelfile
fi
