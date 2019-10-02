#!/usr/bin/env python
#
# ==========================================================================================
# Wrapper for Mira: Sequence assembly with MIRA 5
# ==========================================================================================

import argparse
import os
import subprocess


def handle_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--illumina_paired', '-i', type=argparse.FileType('r'), dest='illumina', default=[], action='append', required=True, help="Illumina paired-end reads")
    parser.add_argument('--ion_torrent', '-t', type=argparse.FileType('r'), dest='iontorrent', required=False, help="Iontorrent single-end reads")    
    parser.add_argument('--roche_454', '-f', type=argparse.FileType('r'), dest='roche', required=False, help="Roche 454 data")
    parser.add_argument('--sanger', '-s', type=argparse.FileType('r'), dest='sanger', required=False, help="Sanger data")
    parser.add_argument('--output', '-o', default='mira.fasta', dest='output_file', required=True, help="Assembly unpadded.")
    parser.add_argument('--large', '-g', default='mira_large_contigs.fasta', dest='output_file_filtered', required=True, help="Assembly unpadded with only large contigs.")
    parser.add_argument('--threads', '-p', type=int, default=1, dest='threads', help="number of threads")
    parser.add_argument('--num_of_passes', '-n', type=int, default=0, dest='num_of_passes', help="Defines how many iterations of the whole assembly process are done")
    parser.add_argument('--kmer_series', '-k', dest='kmer_series', help="Comma separated list of integers at least 0 and smaller or equal to 256, e.g. '17,31,63,127'.")
    parser.add_argument('--rmb_break_loops', '-b', type=int, dest='rmb_break_loops', help="Defines the maximum number of times a contig can be rebuilt during a main assembly pass if misassemblies due to possible repeats are found. integer > 0.")
    parser.add_argument('--automatic_repeat_detection', '-a', dest='automatic_repeat_detection', default="yes", help="on|y[es]|t[rue], off|n[o]|f[alse]")
    parser.add_argument('--coverage_threshold', '-c', type=float, dest='coverage_threshold', help="Default is 2.0 for all sequencing technologies in most assembly cases. float > 1.0")    
    parser.add_argument('--min_length', '-m', type=int, dest='min_length', help="Default is dependent of the sequencing technology, currently 400 for Sanger and 200 for 454 and Ion Torrent. integer > 1")    
    parser.add_argument('--minimum_read_length', '-l', type=int, dest='minimum_read_length',  help="Defines the minimum length that reads must have to be considered for the assembly. Shorter sequences will be filtered out at the beginning of the process and won't be present in the final project. integer at least 20")       
    parser.add_argument('--minimum_reads_per_contig', '-r', type=int, dest='minimum_reads_per_contig', help="Defines the minimum number of reads a contig must have before it is built or saved by MIRA. integer at least 1")    
    parser.add_argument('--rename_prefix', '-x', dest='rename_prefix', help="Rename given read prefix of Illumina data to shorten read names (long read names >40 character will result in stop of mira).")
    options = parser.parse_args()
    return options

def main(options):
    mira_config="manifest.txt"
    s = " "
    with open(mira_config, 'w') as config:
        config.write("# Mainifest describing a de novo assembly\n")
        config.write("# First part: defining some basic options\n")
        config.write("project = mira\n")
        config.write("job = genome,denovo,accurate\n")
        general_parameters = "parameters = COMMON_SETTINGS -GENERAL:number_of_threads=" + str(options.threads) + " -NW:cac=warn -NW:cmrnl=warn" + " \\\n"
        config.write(general_parameters)
        assembly_parameters = " -ASSEMBLY:num_of_passes=" + str(options.num_of_passes)
        if options.kmer_series:
            assembly_parameters += " -ASSEMBLY:kmer_series=" + options.kmer_series
        if options.rmb_break_loops:
            assembly_parameters += " -ASSEMBLY:rmb_break_loops=" + str(options.rmb_break_loops)
        if options.automatic_repeat_detection:
            assembly_parameters += " -ASSEMBLY:automatic_repeat_detection=" + options.automatic_repeat_detection + " \\\n"
        config.write(assembly_parameters)
        assembly_parameters_solexa = "SOLEXA_SETTINGS"
        solexa_set = 0
        if options.coverage_threshold:
            assembly_parameters_solexa += " -ASSEMBLY:coverage_threshold=" + str(options.coverage_threshold)
            solexa_set = 1 
        if options.min_length:
            assembly_parameters_solexa += " -ASSEMBLY:min_length=" + str(options.min_length)
            solexa_set = 1 
        if options.minimum_read_length:
            assembly_parameters_solexa += " -ASSEMBLY:minimum_read_length=" + str(options.minimum_read_length)
            solexa_set = 1 
        if options.minimum_reads_per_contig:
            assembly_parameters_solexa += " -ASSEMBLY:minimum_reads_per_contig=" + str(options.minimum_reads_per_contig)
            solexa_set = 1 
        if solexa_set == 1:
            config.write(assembly_parameters_solexa)
            config.write("\n")
        config.write("\n")
        if options.illumina:
            config.write("# Define the paired-end Illumina reads\n")
            config.write("readgroup = PairedEndIlluminaReads\n")
            config.write("autopairing\n")
            illumina_reads = "data = " + options.illumina[0].name + " " +  options.illumina[1].name + "\n"
            config.write(illumina_reads)
            config.write("technology = solexa\n")
            if options.rename_prefix:
                rename_parameter = "rename_prefix = " + options.rename_prefix + ": M1" + "\n"
                config.write(rename_parameter)
            config.write("\n")
        if options.iontorrent:
            config.write("# Define the Iontorrent single-end reads\n")
            config.write("readgroup = Iondata\n")
            iontorrent_reads = "data = " + options.iontorrent.name + "\n"
            config.write(iontorrent_reads)
            config.write("technology = iontor\n")    
            config.write("\n")
        if options.roche:
            config.write("# Define the shotgun 454 data\n")
            config.write("readgroup = DataForShotgun454\n")
            roche_reads = "data = " + options.roche.name + "\n"
            config.write(roche_reads)
            config.write("technology = 454\n")  
            config.write("\n")
        if options.sanger:
            config.write("# Define Sanger data\n")
            config.write("readgroup = DataForSanger\n")
            sanger_reads = "data = " + options.sanger.name + "\n"
            config.write(sanger_reads)
            config.write("technology = sanger\n")  
    config.close()
    # Run Mira with manifest File
    subprocess.call(["mira", "manifest.txt"])
    assembly_out_unpadded="mira_assembly/mira_d_results/mira_out.unpadded.fasta"
    assembly_out_unpadded_filtered="mira_assembly/mira_d_results/mira_LargeContigs_out_StrainX.unpadded.fasta"
    if os.path.isfile(assembly_out_unpadded):
        os.rename(assembly_out_unpadded, options.output_file)
    else:
        print("Mira output assembly mira_assembly/mira_d_results/mira_result_out.unpadded.fasta not found!")
        os._exit(1)
    if os.path.isfile(assembly_out_unpadded_filtered):
        os.rename(assembly_out_unpadded_filtered, options.output_file_filtered)
    else:
        print("Mira output assembly with large contigs mira_assembly/mira_d_results/mira_LargeContigs_out_StrainX.unpadded.fasta not found!")
        os._exit(1)

if __name__ == '__main__':
    options = handle_args()
    main(options)
