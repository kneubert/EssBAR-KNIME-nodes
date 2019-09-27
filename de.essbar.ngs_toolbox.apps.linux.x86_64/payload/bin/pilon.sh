#!/bin/bash
# Assembly improvement of reference-based scaffolds with Pilon
assembly=$1
reads=$2
outfile=$3
outdir=$4
shift 4
pilon_args=$*

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
bowtie_dir=$DIR/bowtie2-2.3.5
echo "mkdir -p $outdir"
mkdir -p $outdir
echo "cd $outdir"
cd $outdir

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

# Map paired-end reads to assembly using bowtie2 version 2.3.5 (which supports interleaved reads)
echo "$bowtie_dir/bowtie2-build --quiet $assembly $assembly"
$bowtie_dir/bowtie2-build --quiet $assembly $assembly
echo "$bowtie_dir/bowtie2 -x $assembly --interleaved $reads -S frags.sam -p $threads"
$bowtie_dir/bowtie2 -x $assembly --interleaved $reads -S frags.sam -p $threads

echo "samtools view -bS frags.sam | samtools sort -o frags.bam"
samtools view -bS frags.sam | samtools sort -o frags.bam
rm frags.sam

echo "samtools index frags.bam"
samtools index frags.bam

# Run Pilon
echo "java -Xmx16G -jar $DIR/pilon-1.23.jar --genome $assembly --frags frags.bam --outdir $outdir $pilon_args"
java -Xmx16G -jar $DIR/pilon-1.23.jar --genome $assembly --frags frags.bam --outdir $outdir $pilon_args

# Corrected assembly
echo "cp $outdir/pilon.fasta $outfile"
cp $outdir/pilon.fasta $outfile
