#!/bin/bash
## Wrapper script for ABACAS
reference=$1
OUT=$2
shift
shift

# Joint multifasta reference
echo "joinMultifasta.pl $reference Refsequence.union.fasta"
joinMultifasta.pl $reference Refsequence.union.fasta

# Run ABACAS
echo "abacas.1.3.1.pl -r Refsequence.union.fasta $* -o abacas"
abacas.1.3.1.pl -r Refsequence.union.fasta $* -o abacas 

# Split multifasta result according to reference genome
echo "splitABACASunion.pl $reference Refsequence.union.fasta abacas.fasta abacas.crunch abacas.tab"
splitABACASunion.pl $reference Refsequence.union.fasta abacas.fasta abacas.crunch abacas.tab

echo "cat Split.ABACAS.fasta abacas.contigsInbin.fas >$OUT"
cat Split.ABACAS.fasta abacas.contigsInbin.fas >$OUT
