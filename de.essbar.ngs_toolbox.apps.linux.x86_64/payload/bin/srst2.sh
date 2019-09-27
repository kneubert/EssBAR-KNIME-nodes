#!/bin/bash

# Wrapper script to srst2 that accepts the same arguments except
# for the input files.
# first, parse the options:

flag="--input_se"
reads=""
mlstdb=""
mlstdef_file=""
mlstfasta_file=""

echo $*
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
		    	flag="--input_pe"
                ;;
			mlst_db)
                mlstdb="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    	echo "Parsing option: '--${OPTARG}', value: '${mlstdb}'" >&2;
				txt_files=( ${mlstdb}/*/*.txt )
				fasta_files=( ${mlstdb}/*/*.fasta )
				mlstdef_file=`echo ${txt_files[0]}`
				mlstfasta_file=`echo ${fasta_files[0]}`
		    	echo "MLST def file $mlstdef_file"
				echo "MLST fasta file $mlstfasta_file"
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

# remove --mlst_db parameter and value
for arg do 
   shift
   [ "$arg" = "--mlst_db" ] || [ "$arg" = "${mlstdb}" ]  && continue
   set -- "$@" "$arg"
done


# check if the file with ST profiles for mlst database exists
if [[ ! -e $mlstdef_file ]];
then
   echo ERROR file with ST profiles $mlstdef_file does not exist. 1>&2
   exit 1
fi

# check if the fasta file *fasta for mlst db exists
if [[ ! -e $mlstfasta_file ]];
then
   echo ERROR Fasta file with alleles $mlstfasta_file does not exist. 1>&2
   exit 1
fi

# THEN, access the positional params
echo "there are $# positional params remaining"
for ((i=1; i<=$#; i++)); do
  printf "%d\t%s\n" $i "${!i}"
done

# run SRST2
echo "srst2 $* ${flag} ${reads} --mlst_db $mlstfasta_file --mlst_definitions $mlstdef_file"
srst2 $* ${flag} ${reads} --mlst_db $mlstfasta_file --mlst_definitions $mlstdef_file