#!/usr/bin/env python

import sys,os, glob
import subprocess


def handle_args():
    import argparse
    dirpath = os.getcwd()
    default_output = os.path.join(dirpath, 'output_profile.txt')
    default_reads = os.path.join(dirpath, 'reads.fq')
    usage = ""
    usage += "stringMLST: Fast k-mer based multi locus sequence typing (MLST) ."
    parser = argparse.ArgumentParser(description = usage )
    parser.add_argument('--fastq1', 
                              dest='fastq1', 
                              required=True,
                              help='Path to first fastq file for paired end sample and path to the fastq file for single end file.')
    parser.add_argument('--fastq2', 
                              dest='fastq2', 
                              help='Path to second fastq file for paired end sample.')
    parser.add_argument('--config', 
                              dest='config', 
                              required=True,
                              help='Config file : is a tab delimited file which has the information for typing scheme ie loci, its multifasta file and profile definition file.')
    parser.add_argument('--coverage', 
                              action="store_true",
                              help='Calculate seqence coverage for each allele. Turns on read generation (-r) and turns off fuzzy (-z 1). Requires bwa, bamtools and samtools be in your path')
    parser.add_argument('--output', 
                              dest='output', 
                              default = default_output,
                              help='Prints the output to a file.')
    parser.add_argument('-k', 
                              dest='kmer_length', 
                              default=35,
                              help='Kmer length for which the db was created(Default k = 35). Could be verified by looking at the name of the db file. Could be used if the reads are of very bad quality or have a lot of Ns.')
    parser.add_argument('-r', 
                              dest='reads', 
			      default=default_reads,
                              help='A seperate reads file is created which has all the reads covering all the locus.')
    parser.add_argument('-z', 
                              dest='fuzzy_thresh', 
                              default=300,
                              help='Threshold for reporting a fuzzy match (Default=300). For higher coverage reads this threshold should be set higher to avoid indicating fuzzy match when exact match was more likely. For lower coverage reads, threshold of <100 is recommended')
    return parser.parse_args()


def buildDB(prefix):
    print("Build DB for stringMLST with prefix " + prefix)
    command='stringMLST.py --buildDB -c %s -k %s -P %s' % (options.config, options.kmer_length, prefix)
    print command
    p = subprocess.Popen(["stringMLST.py", "--buildDB", "-c", options.config, "-k", str(options.kmer_length), "-P", prefix], stdout=subprocess.PIPE)
    out = p.stdout.read()
    print out

def predict(prefix):
    print("Predict allele profiles for kmer DB with prefix " + prefix)
    coverage_flag = ""
    if options.coverage:
       coverage_flag = "-C"
    if options.fastq2:
       command_args = ["stringMLST.py", "--predict", "-1", options.fastq1, "-2",options.fastq2, "-k", str(options.kmer_length), "-P", prefix, "-o", options.output, "-r", "--fuzzy", str(options.fuzzy_thresh), coverage_flag]
       print " ".join(command_args)
       p = subprocess.Popen(command_args, stdout=subprocess.PIPE)
       out = p.stdout.read()
       print out
    else:
       command_args = ["stringMLST.py", "--predict", "-1", options.fastq1, "-s", "-k", str(options.kmer_length), "-P", prefix,"-o", options.output, "-r", "--fuzzy", str(options.fuzzy_thresh), coverage_flag]
       print " ".join(command_args)
       p = subprocess.Popen(command_args, stdout=subprocess.PIPE)
       out = p.stdout.read()
       print out
   
    
    
def main(options):
    output_dir=os.path.dirname(options.output)
    db_prefix=os.path.join(output_dir, "k_mer_DB")
    buildDB(db_prefix)
    predict(db_prefix)
    output_reads = glob.glob('*_reads.fq')
    print("rename file " + output_reads[0] + " to " + options.reads)
    os.rename(output_reads[0], options.reads)

if __name__ == '__main__':
    options = handle_args()
    main(options)
