#!/bin/bash
## Wrapper script for ABACAS
contigs=$1
reference=$2
OUT=$3
OUT2=$4
OUTP=$5

shift 5
echo "contigs: $contigs"
echo "reference: $reference"
echo "output file 1: $OUT"
echo "output file 2: $OUT2"
echo "output prefix: $OUTP"
echo $*
outdir=`dirname $OUTP`

#echo "cd $outdir"
#cd $outdir

# Join multifasta reference
echo "joinMultifasta.pl $reference $outdir/Refsequence.union.fasta"
joinMultifasta.pl $reference $outdir/Refsequence.union.fasta

# Run ABACAS
cd $outdir
echo "abacas.1.3.1.pl -r $outdir/Refsequence.union.fasta -q $contigs -m -t -b $* -o $OUTP"
abacas.1.3.1.pl -r $outdir/Refsequence.union.fasta -q $contigs -m -t -b $* -o $OUTP

# Split multifasta result according to reference genome
echo "splitABACASunion.pl $reference $outdir/Refsequence.union.fasta ${OUTP}.fasta  ${OUTP}.crunch ${OUTP}.tab"
splitABACASunion.pl $reference $outdir/Refsequence.union.fasta ${OUTP}.fasta  ${OUTP}.crunch ${OUTP}.tab


# Reference-mapped contigs
echo "mv $outdir/Split.ABACAS.fasta $OUT"
mv $outdir/Split.ABACAS.fasta $OUT

# Unmapped contigs
echo "mv ${OUTP}.contigsInbin.fas $OUT2"
mv ${OUTP}.contigsInbin.fas $OUT2
