#!/usr/bin/env python3

import sys
import os
from subprocess import Popen, PIPE, call
#from concurrent.futures.thread import ThreadPoolExecutor
#from concurrent.futures import ProcessPoolExecutor
from Bio import Entrez


def handle_args():
    import argparse
    usage = ""
    usage += "Create genus databases for prokka."
    parser = argparse.ArgumentParser(description = usage )
    parser.add_argument('-g', dest='genus', help='genus names separated by comma.')
    parser.add_argument('-o', dest='output_dir', help='output folder')
    args = parser.parse_args(sys.argv[1:])
    return args


def get_tax_ids(species):
    """to get data from ncbi taxomomy, we need to have the taxid. we can
    get that by passing the species name to esearch, which will return
    the tax id"""
    species = species.replace(' ', "+").strip()
    sterm = species + " AND " + "(bacteria[Division] OR viruses[Division])"
    search = Entrez.esearch(term = sterm, db = "taxonomy", retmode = "xml")
    record = Entrez.read(search)
    #print(record['IdList'])
    return record['IdList']

def get_tax_data(taxid):
    """once we have the taxid, we can fetch the record"""
    search = Entrez.efetch(id = taxid, db = "taxonomy", retmode = "xml")
    return Entrez.read(search)

def call_database_maker(genus):
    print("call database maker for genus " + genus)
    taxids = get_tax_ids(genus) 
    taxids_str = " ".join(taxids)
    print("taxid(s): " + taxids_str)
    data = get_tax_data(taxids)
    lineage = {d['Rank']:d['ScientificName'] for d in data[0]['LineageEx'] if d['Rank'] in ['phylum']}
    # download_dir = options.output_dir + "/" + "tmpdir_" + genus
    tmp_dir = "tmpdir_" + genus
    download_dir = os.path.join(options.output_dir, tmp_dir)
    #subprocess.call(["database_maker.py -t '%s' -d '%s' -n '%s' -o '%s'" %(taxids, options.output_dir, genus, download_dir )])
    #pi = subprocess.Popen(['database_maker.py -t %s -d %s -n %s -o %s' % (taxids_str, options.output_dir, genus, download_dir )], shell=True)
    print("download references to tmp directory " + download_dir)
    os.system('database_maker.py -t %s -d %s -n %s -o %s' % (taxids_str, options.output_dir, genus, download_dir ))
    #p = Popen(['database_maker.py -t %s -d %s -n %s -o %s' % (taxids_str, options.output_dir, taxids_str, download_dir )], shell=True, stdout=PIPE, stderr=PIPE)
    #output = p.communicate()[0].decode(encoding='UTF-8')
    pre_db_file = os.path.join(download_dir,"pre_db_files",genus)
    call(['cp', pre_db_file, options.output_dir])
    call(['rm', '-R', download_dir])
    
    
def main(options):
    genus_list = options.genus.split(",")
    Entrez.email = ""
    print('parsing taxonomic data...') # message declaring the parser has begun
    for genus in genus_list:
        call_database_maker(genus)

if __name__ == '__main__':
    options = handle_args()
    main(options)
