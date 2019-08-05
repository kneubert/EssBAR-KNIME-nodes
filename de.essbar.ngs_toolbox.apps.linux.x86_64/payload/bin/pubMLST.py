#!/usr/bin/env python

import sys,os, glob
import subprocess

def handle_args():
    import argparse
    dirpath = os.getcwd()
    default_output = dirpath
    default_blast_output = os.path.join(dirpath, "blast")
    default_config = os.path.join(dirpath, "config.txt")
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
    parser.add_argument('-o', 
                              dest='output', 
                              default = default_output,
                              help='output directory for allele sequences and profiles')
    parser.add_argument('-b', 
                              dest='blast', 
                              default = default_blast_output,
                              help='output directory for blast database of allele sequences')
    parser.add_argument('-c', 
                              dest='config', 
                              default = default_config,
                              help='configuration file for allele sequences and profiles')
    return parser.parse_args()


    
def main(options):
    print("run getMLST_DB.py for " + options.species + "...")
    if options.scheme:
       command_args = ["getmlst_DB.py","--species", options.species,"--repository_url", options.url,"--force_scheme_name", options.scheme, "--output_dir", options.output, "--config", options.config]
       print " ".join(command_args)
       p = subprocess.Popen(command_args, stdout=subprocess.PIPE)
       out = p.stdout.read()
       print out
    else:
	   command_args = ["getmlst_DB.py","--species", options.species,"--repository_url", options.url, "--output_dir", options.output, "--config", options.config]
	   print " ".join(command_args)
	   p = subprocess.Popen(command_args, stdout=subprocess.PIPE)
	   out = p.stdout.read()
	   print out
    print("Create blast db...")
    command_args = ["mlst-make_blast_db", options.output, options.blast]
    print " ".join(command_args)
    p = subprocess.Popen(command_args, stdout=subprocess.PIPE)
    out = p.stdout.read()
    print out

if __name__ == '__main__':
    options = handle_args()
    main(options)
