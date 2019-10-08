#!/bin/bash
# Wrapper for kSNP3 that takes the same arguments except
# a folder for the input assemblies
# and an optional folder for references assemblies


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


if [[ $# -lt 4 ]]
then
  echo "arguments -a [asssemblies] -k [int] required for kSNP3, exit"
  exit
fi

min_frac_flag=""
cpu_flag=""
GENBANK_flag=""
SNPs_all_flag=""
CORE_flag=""
ML_flag=""
NJ_flag=""
VCF_flag=""
ALL_anno_flag=""
tree_file="parsimony.tre"

# options may be followed by one colon to indicate they have a required argument
if ! options=$(getopt -o a:r:g:o:t:k:s:f:p:cmnvl -l assemblies:,references:,genbank:,outdir:,tree:,kmer_length:,SNPs_all:,min_frac:,CPU:,core,ML,NJ,vcf,all -- "$@")
then
    # something went wrong, getopt will put out an error message for us
    exit 1
fi

#set -- $options

while [ $# -gt 0 ]
do
  case "$1" in
    -a | --assemblies ) assemblies="$2"; shift 2 ;;
    -r | --references ) references="$2"; shift 2 ;;
    -g | --genbank ) GENBANK_flag="-genbank $2"; shift 2 ;;
    -o | --outdir ) outdir="$2"; shift 2 ;;
    -t | --tree ) tree_file="$2"; shift 2 ;;
    -k | --kmer_length ) kmer_length="$2"; shift 2 ;;
    -s | --SNPs_all ) SNPs_all_flag="-SNPs_all $2"; shift 2 ;;
    -f | --min_frac ) min_frac_flag="-min_frac $2"; shift 2 ;;
    -p | --CPU ) cpu_flag="-CPU $2"; shift 2 ;;
    -c | --core ) CORE_flag="-core" ; shift ;;
    -m | --ML ) ML_flag="-ML"; shift ;;
    -n | --NJ ) NJ_flag="-NJ"; shift ;;
    -v | --vcf ) VCF_flag="-vcf"; shift ;;
    -l | --all ) ALL_anno_flag="-all_annotations"; shift ;;
    -- ) echo "unknown option $1"; shift; break ;;
    * ) break ;;
  esac
done

if [[ ! $assemblies ]] 
then
  echo "Directory with assemblies required as input"
  exit
fi

echo ln -s $assemblies .
ln -s $assemblies .

assemblies=`basename $assemblies`

if [[ ! $outdir ]] 
then
  echo "Output Directory required as input"
  exit
fi

if [[ ! $kmer_length ]] 
then
  echo "kmer_length required as input"
  exit
fi


# create assembly list file
echo "$DIR/MakeKSNP3infile $assemblies in_list.txt A" 
$DIR/MakeKSNP3infile $assemblies in_list.txt A

anno_flag=''

# create reference list file (optional)
if [[ $references ]] 
then
   echo ln -s $references .
   ln -s $references .
   references =`basename $references`
   echo "$DIR/MakeKSNP3infile $references ref_list.txt A"
   $DIR/MakeKSNP3infile $references ref_list.txt A
   anno_flag="-annotate ref_list.txt"
 
   # check if reference assemblies are in assembly list, otherwise add them
   ids=`cut -f 2 ref_list.txt`
   for id in $ids
   do 
     GVAR=""
     GVAR=`grep $id in_list.txt`
     if [ -z "$GVAR" ]; 
     then
        grep $id ref_list.txt >>in_list.txt
     fi
   done
fi

file_flags="$SNPs_all_flag $GENBANK_flag"
binary_flags="$CORE_flag $ML_flag $min_frac_flag $cpu_flag $NJ_flag $VCF_flag $ALL_anno_flag"

# run kSNP3
echo ${DIR}/kSNP3 -in in_list.txt $anno_flag -outdir $outdir -k $kmer_length $file_flags $binary_flags
${DIR}/kSNP3 -in in_list.txt $anno_flag -outdir "$outdir" -k $kmer_length $file_flags $binary_flags

if [ -f "$outdir/tree.parsimony.tre" ]
then
  cp $outdir/tree.parsimony.tre $tree_file
else
  echo "Output file for parsimony tree $outdir/tree.parsimony.tre not found."
fi
