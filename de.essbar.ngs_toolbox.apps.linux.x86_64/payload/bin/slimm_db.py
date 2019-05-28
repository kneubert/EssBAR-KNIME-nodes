#!/usr/bin/env python

'''
@name: slimmDB.py
@author: Kerstin Neubert <kerstin.neubert@fu-berlin.de>
@update: 20-August-2018
please type "./slimmDB.py -h " for usage help
'''

'''Import modules, and initialize variables'''
#=============================================#
try : 
	import os, sys, subprocess
	import argparse
	import tarfile, gzip
except : 
	sys.stderr.write('ERROR! Cannot import required modules os, sys, subprocess, argparse, tarfile, gzip.\n')


parser = argparse.ArgumentParser(description="Build a user-defined Slimm taxon database")
group = parser.add_argument_group('Required arguments')
group.add_argument('-f', action='store', dest='fasta', required=True, help='Genomes in FASTA or gzipped FASTA format (with NCBI accessions).')
group.add_argument('-o', action='store', dest='slimmdb', required=True, help='Slimm database output file (*.sldb file).')
group.add_argument('-b', action='store', dest='max_mapping', required=False, default=1000000, help="maximum number of mapping to load to memory. (default=1000000).")

args = parser.parse_args()
fasta = os.path.abspath(args.fasta)
slimmdb = os.path.abspath(args.slimmdb)
retval = os.getcwd()

#print os.path.dirname(os.path.abspath(args.slimmdb)) 
tmp_path = os.path.join(os.path.dirname(os.path.abspath(args.slimmdb)), "_tmpdir")
if not os.path.exists(tmp_path):
	os.makedirs(tmp_path)
	
taxdump_file = os.path.join(tmp_path, "taxdump.tar.gz")
nodes_file = os.path.join(tmp_path,"nodes.dmp")
names_file = os.path.join(tmp_path,"names.dmp")
# download and extract nodes.dmp and names.dmp files from NCBI
subprocess.call(["curl", "ftp://ftp.ncbi.nih.gov/pub/taxonomy/taxdump.tar.gz", "-o", taxdump_file, "--silent"])
tar = tarfile.open(taxdump_file)
tar.extractall(path=tmp_path)
tar.close()
os.unlink(taxdump_file)

# download and extract accesssion2taxid files from NCBI
deadnucl_file = os.path.join(tmp_path, "dead_nucl.accession2taxid.gz")
nuclwgs_file = os.path.join(tmp_path, "nucl_wgs.accession2taxid.gz")
nuclgb_file = os.path.join(tmp_path, "nucl_gb.accession2taxid.gz")

subprocess.call(["curl", "ftp://ftp.ncbi.nih.gov/pub/taxonomy/accession2taxid/dead_nucl.accession2taxid.gz", "-o", deadnucl_file, "--silent"])
subprocess.call(["curl", "ftp://ftp.ncbi.nih.gov/pub/taxonomy/accession2taxid/nucl_wgs.accession2taxid.gz", "-o", nuclwgs_file, "--silent"])
subprocess.call(["curl", "ftp://ftp.ncbi.nih.gov/pub/taxonomy/accession2taxid/nucl_gb.accession2taxid.gz", "-o", nuclgb_file, "--silent"])

with gzip.open(deadnucl_file, 'rb') as f_in, open(deadnucl_file.rsplit( ".", 1 )[ 0 ] , 'wb') as f_out:        
    f_out.writelines(f_in)
os.unlink(deadnucl_file)

with gzip.open(nuclwgs_file, 'rb') as f_in, open(nuclwgs_file.rsplit( ".", 1 )[ 0 ] , 'wb') as f_out:        
    f_out.writelines(f_in)
os.unlink(nuclwgs_file)
    
with gzip.open(nuclgb_file, 'rb') as f_in, open(nuclgb_file.rsplit( ".", 1 )[ 0 ] , 'wb') as f_out:        
    f_out.writelines(f_in)
os.unlink(nuclgb_file)

deadnucl_file = deadnucl_file.rsplit( ".", 1 )[ 0 ] 
nuclwgs_file = nuclwgs_file.rsplit( ".", 1 )[ 0 ] 
nuclgb_file = nuclgb_file.rsplit( ".", 1 )[ 0 ]

# call slimm_build 
os.chdir(tmp_path)
p = subprocess.Popen(["slimm_build","-v", "-b", str(args.max_mapping), "-nm", names_file, "-nd", nodes_file, "-o", slimmdb, fasta, deadnucl_file, nuclwgs_file, nuclgb_file], stdout=subprocess.PIPE)
output, err = p.communicate()
print  output
os.chdir(retval)
subprocess.call(["rm", "-rf", tmp_path])
