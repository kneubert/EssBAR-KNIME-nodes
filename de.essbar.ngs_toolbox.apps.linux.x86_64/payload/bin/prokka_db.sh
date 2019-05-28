#!/bin/bash
# Build Prokka DB: kingdoms based on UniProt, Genus-DBs based on Reference assemblies using 'database_maker.py'
# e.g.
# ./prokka_db.sh --bacteria --archaea --fungi --viruses --genus Brucella,Francisella --output prokka_db

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
prokka_bin=$DIR/prokka/bin

export PERL5LIB=$DIR/../lib:$PERL5LIB

optspec=":hv-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                archaea)
                    echo "Parsing option: '--${OPTARG}'" >&2
		    archaea_db=1
                    ;;
                bacteria)
                    echo "Parsing option: '--${OPTARG}'" >&2
                    bacteria_db=1
                    ;;
                fungi)
                    echo "Parsing option: '--${OPTARG}'" >&2
		    fungi_db=1
                    ;;
		viruses)
		    echo "Parsing option: '--${OPTARG}'" >&2
		    viruses_db=1
                    ;;
		genus) 
		    genus_dbs="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value '${genus_dbs}'" >&2
                    ;;
		output)
		    output_dir="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    echo "Parsing option: '--${OPTARG}', value: '${output_dir}'" >&2
		    ;;
		help)
                    echo "usage: $0 [-v] [--bacteria] [--archaea] [--fungi] [--viruses] [--genus <GENUS,...,GENUS>] [--output <directory>]" >&2
            	    exit 2
                    ;;
                *)
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                    fi
                    ;;
            esac;;
        h)
            echo "usage: $0 [-v] [--bacteria] [--archaea] [--fungi] [--viruses] [--genus <GENUS,...,GENUS>] [--output <directory>]" >&2

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

if [ ! -n "$output_dir" ]
then
 echo "output directory not set, use --output <directory>"
 exit 2
fi

mkdir -p $output_dir
cd $output_dir

echo "Build kingdom databases from Uniprot..."
if [ -n "$bacteria_db" ]
then
  if [ ! -e ${output_dir}/kingdom/Bacteria/sprot ]
  then
     echo "Bacteria"
     mkdir -p ${output_dir}/kingdom/Bacteria
     wget ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_bacteria.dat.gz
     zcat uniprot_sprot_bacteria.dat.gz > uniprot_sprot_bacteria.dat
     prokka-uniprot_to_fasta_db --verbose uniprot_sprot_bacteria.dat > ${output_dir}/kingdom/Bacteria/sprot
     rm uniprot_sprot_bacteria.dat
     rm uniprot_sprot_bacteria.dat.gz
  fi
fi

if [ -n "$archaea_db" ]
then
  if [ ! -e ${output_dir}/kingdom/Archaea/sprot ]
  then
     echo "Archaea"
     mkdir -p ${output_dir}/kingdom/Archaea
     wget ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_archaea.dat.gz
     zcat uniprot_sprot_archaea.dat.gz > uniprot_sprot_archaea.dat
     prokka-uniprot_to_fasta_db --verbose uniprot_sprot_archaea.dat > ${output_dir}/kingdom/Archaea/sprot
     rm uniprot_sprot_archaea.dat
     rm uniprot_sprot_archaea.dat.gz
  fi
fi


if [ -n "$fungi_db" ]
then
  if [ ! -e ${output_dir}/kingdom/Fungi/sprot ]
  then
     echo "Fungi"
     mkdir -p ${output_dir}/kingdom/Fungi
     wget ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_fungi.dat.gz
     zcat uniprot_sprot_fungi.dat.gz > uniprot_sprot_fungi.dat
     prokka-uniprot_to_fasta_db --verbose uniprot_sprot_fungi.dat > ${output_dir}/kingdom/Fungi/sprot
     rm uniprot_sprot_fungi.dat
     rm uniprot_sprot_fungi.dat.gz
  fi
fi

if [ -n "$viruses_db" ]
then
  if [ ! -e ${output_dir}/kingdom/Viruses/sprot ]
  then
     echo "Viruses"
     mkdir -p kingdom/Viruses
     wget ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_viruses.dat.gz
     zcat uniprot_sprot_viruses.dat.gz > uniprot_sprot_viruses.dat
     prokka-uniprot_to_fasta_db --verbose uniprot_sprot_viruses.dat > ${output_dir}/kingdom/Viruses/sprot
     rm uniprot_sprot_viruses.dat
     rm uniprot_sprot_viruses.dat.gz
  fi
fi

# download 'cm' and 'hmm' from prokka github
echo "Retrieve 'cm' and 'hmm' from prokka github repository..."
if [ ! -e cm ]
then
  svn export https://github.com/tseemann/prokka/trunk/db/cm
fi

if [ ! -e hmm ]
then
  svn export https://github.com/tseemann/prokka/trunk/db/hmm
fi

db_dir=`pwd`

# build genus-specific database using 'database_maker.py'
if [ "$genus_dbs" ]
then
   echo "Build genus-specific database(s) using 'database_maker'..."
   genus_dir=$db_dir/genus
   #setup_genus_dbs.py -g $genus_dbs -o $genus_dir
   setup_genus_dbs.py -g $genus_dbs -o $genus_dir
fi

#export PROKKA_DB=$output_dir
# index PROKKA databases
echo "Index Prokka databases..."
prokka --dbdir ${output_dir} --setupdb
