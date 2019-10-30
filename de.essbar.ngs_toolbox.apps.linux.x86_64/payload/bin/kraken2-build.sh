#!/bin/bash
# Wrapper script for kraken2-build
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
kraken2_bin=$DIR

db_flag=("archaea" "bacteria" "plasmid" "viral" "human" "fungi" "plant" "protozoa" "nt" "env_nt" "UniVec" "UniVecCore")
db_set=(0 0 0 0 0 0 0 0 0 0 0 0)
threads_flag=""
kmer_length_flag=""
minimizer_length_flag=""
minimizer_spaces_flag=""
max_db_size_flag=""
ftp_flag=""

echo "kraken2-build.sh $*"

optspec=":hv-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                archaea)
                    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[0]=1
                    ;;
                bacteria)
                    echo "Parsing option: '--${OPTARG}'" >&2
                    db_set[1]=1
                    ;;
                plasmid)
                    echo "Parsing option: '--${OPTARG}'" >&2
                    db_set[2]=1
                    ;;
		viral)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[3]=1
                    ;;
		human)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[4]=1
                    ;;
                fungi)
                    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[5]=1
                    ;;
		plant)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[6]=1
                    ;;
		protozoa)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[7]=1
                    ;;
		nt)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[8]=1
                    ;;
		env_nt)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[9]=1
                    ;;
		UniVec)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[10]=1
                    ;;
		UniVec_Core)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    db_set[11]=1
                    ;;
		add-to-library) 
		    fasta_directory="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value '${fasta_directory}'" >&2
                    ;;
		db)
		    db_prefix="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value: '${db_prefix}'" >&2
		    ;;
		threads)
		    threads="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    threads_flag="--threads $threads"
		    echo "Parsing option: '--${OPTARG}', value: '${threads}'" >&2
		    ;;
		kmer-len)
		    kmer_length="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    kmer_length_flag="--kmer-len $kmer_length"
		    echo "Parsing option: '--${OPTARG}', value: '${kmer_length}'" >&2
		    ;;
		minimizer-len)
		    minimizer_length="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    minimizer_length_flag="--minimizer-len $minimizer_length"
		    echo "Parsing option: '--${OPTARG}', value: '${minimizer_length}'" >&2
		    ;;
		minimizer-spaces)
		    minimizer_spaces="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    minimizer_spaces_flag="--minimizer-spaces $minimizer_spaces"
		    echo "Parsing option: '--${OPTARG}', value: '${minimizer_spaces}'" >&2
		    ;;
		max-db-size)
		    max_db_size="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    max_db_size_flag="--max-db-size $max_db_size"
		    echo "Parsing option: '--${OPTARG}', value: '${max_db_size}'" >&2
		    ;;
		use-ftp)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    ftp_flag="--use-ftp"
                    ;;
		help)
                    echo "usage: $0 [--bacteria] [--archaea] [--fungi] [--viruses] [--add-to-library <folder>] [--db <prefix>]" >&2
            	    exit 2
                    ;;
                *)
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                    fi
                    ;;
            esac;;
        h)
            echo "usage: $0 [-v] [--bacteria] [--archaea] [--fungi] [--viruses] [--genus <GENUS,...,GENUS>] [--db <prefix>]" >&2

            exit 2
            ;;
        v)
            echo "Parsing option: '-${optchar}'" >&2
            ;;
        *)
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

total=0
for n in ${db_set[@]};
do
  ((total += n))
done

if [ ! -n "$db_prefix" ]
then
 echo "Output prefix for Kraken2 DB not set, use --db <prefix>"
 exit 2
fi


if [[ "$total" -eq 0 && ! -d "$fasta_directory" ]];
then
  standard_db=1
fi


if [ -n "$standard_db" ]
then
  # Build Kraken2 standard db (default)
  echo kraken2-build --standard --db $db_prefix $threads_flag $kmer_length_flag $minimizer_length_flag $minimizer_spaces_flag $max_db_size_flag $ftp_flag
  kraken2-build --standard --db $db_prefix $threads_flag $kmer_length_flag $minimizer_length_flag $minimizer_spaces_flag $max_db_size_flag $ftp_flag
  echo kraken2-build --clean --db $db_prefix
  kraken2-build --clean --db $db_prefix
else
  # 1. downlaod taxonomy
  echo kraken2-build --download-taxonomy --db $db_prefix $threads_flag
  kraken2-build --download-taxonomy --db $db_prefix $threads_flag
  # 2. download one or more reference libraries
  # pre-defined references
  for ((i=0; i<${#db_flag[@]}; i++));
  do
    if [[ "${db_set[$i]}" -eq 1 ]];
    then
       echo kraken2-build --download-library ${db_flag[$i]} --db $db_prefix $threads_flag
       kraken2-build --download-library ${db_flag[$i]} --db $db_prefix $threads_flag
    fi
  done
  # user-defined references (fasta input)
  if [ -d "${fasta_directory}" ];
  then 
    for file in ${fasta_directory}/*.fa ${fasta_directory}/*.fasta;
    do 
      echo kraken2-build --add-to-library $file --db $db_prefix $threads_flag
      kraken2-build --add-to-library $file --db $db_prefix $threads_flag
    done
  fi
  # 3. Build the database
  echo kraken2-build --build --db $db_prefix $threads_flag $kmer_length_flag $minimizer_length_flag $minimizer_spaces_flag $max_db_size_flag $ftp_flag
  kraken2-build --build --db $db_prefix $threads_flag $kmer_length_flag $minimizer_length_flag $minimizer_spaces_flag $max_db_size_flag $ftp_flag
  # 4. Cleanup
  echo kraken2-build --clean --db $db_prefix
  kraken2-build --clean --db $db_prefix
fi

