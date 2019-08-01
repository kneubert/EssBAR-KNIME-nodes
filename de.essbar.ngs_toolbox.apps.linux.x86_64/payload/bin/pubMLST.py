#!/usr/bin/env python

import sys,os, glob
import subprocess

def handle_args():
    import argparse
    dirpath = os.getcwd()
    default_fasta = os.path.join(dirpath, 'alleles.fasta')
    default_profile = os.path.join(dirpath, 'profiles.txt')
    default_config = os.path.join(dirpath, 'config.txt')
    usage = ""
    usage += "Download MLST datasets by species from pubmlst.org."
    parser = argparse.ArgumentParser(description = usage )
    parser.add_argument('-s', 
                              dest='species', 
                              required = True,
                              help='The name of the species that you want to download (e.g. "Escherichia coli")')
    parser.add_argument('-n', 
                              dest='scheme', 
                              help='Force downloading of specific scheme name')
    parser.add_argument('-r', 
                              dest='url', 
                              default = 'http://pubmlst.org/data/dbases.xml',
                              help='URL for MLST repository XML index')
    parser.add_argument('-f', 
                              dest='fasta', 
                              default = default_fasta,
                              help='output fasta file with allele sequences')
    parser.add_argument('-p', 
                              dest='profile', 
                              default = default_profile,
                              help='output file with allele profiles')
    parser.add_argument('-c', 
                              dest='config', 
                              default = default_config,
                              help='configuration file for allele sequences and profiles')
    #args = parser.parse_args(sys.argv[1:])
    return parser.parse_args()
    #return args


def create_config(output_dir):
    print("create config file for MLST data in " + output_dir)
    allele_files = glob.glob('*.tfa')
    output_dir_profile=os.path.dirname(options.profile)
    if output_dir_profile == '':
       output_dir_profile = os.getcwd()
    profile_file = os.path.join(output_dir_profile, os.path.basename(options.profile))
    f = open(options.config, "w")
    f.write("[loci]\n")
    for file in allele_files:
       file_path = os.path.join(output_dir, file)
       locus = os.path.splitext(file)[0]
       f.write(locus)
       f.write(" ")
       f.write(file_path)
       f.write("\n")
    f.write("[profile]\n")
    profile = os.path.basename(profile_file)
    f.write(os.path.splitext(profile)[0])
    f.write(" ")
    f.write(profile_file)
    f.write("\n")
    f.close()
    
    
def main(options):
    output_dir=os.path.dirname(options.fasta)
    if output_dir == '':
       output_dir = os.getcwd()
    os.chdir(output_dir)
    print("run getMLST.py for " + options.species + "...")
    if options.scheme:
       #print('getmlst.py --repository_url \"%s\" --species %s --force_scheme_name \"%s\"' % (options.url, options.species, options.scheme ))
       #os.system('getmlst.py --repository_url \"%s\" --species %s --force_scheme_name \"%s\"' % (options.url, options.species, options.scheme ))
       command = 'getmlst.py --repository_url \"%s\" --species %s --force_scheme_name \"%s\"' % (options.url, options.species, options.scheme )
       print(command)
       p = subprocess.Popen(["getmlst.py","--species", options.species,"--repository_url", options.url,"--force_scheme_name", options.scheme], stdout=subprocess.PIPE)
       out = p.stdout.read()
       print out
    else:
       #print('getmlst.py --repository_url \"%s\" --species %s --force_scheme_name \"%s\"' % (options.url, options.species, options.scheme ))
       #os.system('getmlst.py --repository_url %s --species \"%s\"' % (options.url, options.species))
       command = 'getmlst.py --repository_url %s --species \"%s\"' % (options.url, options.species)
       print command
       p = subprocess.Popen(["getmlst.py","--species", options.species,"--repository_url", options.url], stdout=subprocess.PIPE)
       out = p.stdout.read()
       print out
    output_fasta = glob.glob('*.fasta')
    output_fasta = os.path.join(output_dir, output_fasta[0])
    os.rename(output_fasta, options.fasta)
    output_profile = glob.glob('*.txt')
    output_profile = os.path.join(output_dir, output_profile[0])
    os.rename(output_profile, options.profile)
    create_config(output_dir)

if __name__ == '__main__':
    options = handle_args()
    main(options)
