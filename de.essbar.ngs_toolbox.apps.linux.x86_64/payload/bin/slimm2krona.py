#!/usr/bin/env python
#
# ============================================================================== 
# Conversion script: from Slimm output to Krona text input file
# ==============================================================================

import sys
import optparse
import re


def handle_args():
    parser = optparse.OptionParser()
    parser.add_option('-p', '--profile', dest='profile', default='', action='store', help='Slimm profile file')
    parser.add_option('-k', '--krona', dest='krona', default='krona.txt', action='store', help='Krona text output file that can be used by ktTextImport')
    (options, spillover) = parser.parse_args()
    if not options.profile or not options.krona:
        parser.print_help()
        sys.exit()
        
    return options

def main(options):
    
    #re_lin_candidates = re.compile(r"species|superkingdom|phylum|class|order|family|genus|species\t")
    re_lin_candidates = re.compile(r"s__|unclassified\t")
    re_replace = re.compile(r"\|\w__|\w__")

    #hierarchy = ["superkingdom", "phylum", "class", "order", "family", "genus", "species", "strain"]
    
    slimm_profile = list()
    with open(options.profile,'r') as f:
        slimm_profile = f.readlines()
    f.close()

    krona_tmp = options.krona 
    slimm_FH = open(krona_tmp, 'w')


    for line in (slimm_profile):
        x = line.rstrip('\n')
        x_cells = x.split('\t')
        if(re.search(re_lin_candidates, x_cells[2])):
            lineage = re.sub(re_replace, '\t', x_cells[2]) 
            abundance = float(x_cells[3]) 
            slimm_FH.write('%s\n'%(str(abundance) + lineage))
    slimm_FH.close()

if __name__ == '__main__':
    options = handle_args()
    main(options)
