#!/bin/bash
# Assembly improvement of reference-based scaffolds with Pilon
assembly=$1
reads=$2
outfile=$3
shift 3
pilon_args=$*

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
outdir=`dirname $outfile`

optspec=":hv-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
		threads)
		    threads="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
		    threads_flag="--threads $threads"
		    echo "Parsing option: '--${OPTARG}', value: '${threads}'" >&2
		    ;;
                *)
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                    fi
                    ;;
            esac;;

        *)
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

# Map paired-end reads to assembly using bowtie2
bowtie2-build $assembly $assembly
bowtie2 -x $assembly --interleaved $reads -S frags.sam -p $threads
samtools view -bS frags.sam | samtools sort -o frags.bam
samtools index frags.bam

# Run Pilon
java -Xmx16G -jar $DIR/pilon-1.23.jar --genome $assembly --frags frags.bam --outdir $outdir $pilon_args

# Corrected assembly
mv $outdir/pilon.fasta $outfile