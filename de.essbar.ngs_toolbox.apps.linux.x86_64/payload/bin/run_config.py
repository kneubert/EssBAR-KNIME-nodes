#!/usr/bin/env python
# This code can run for simulation or real data.
# This program can have three or four parameters. Required is the name of the tree, fasta file of genomes, and file listing the SNPs (format is 5 tab-delimited columns: locusID, SNP context, SNP allele, position (space) strand (F or R), and genome ID), with loci separated by blank line, as output by kSNP SNPs_all file.Optional 4th parameter is the name of the master genome.
# $ run_config.py [tree name] [genome_fasta] [SNPs_file] <master genome name>

import os, sys, time

# True: it will run simulation, and compare the result with the ground truth
# False: this is for real data
simulation = False


# Path of the hgtFinder
my_bin = '/usr/gapps/kpath/hreFinder/bin'	# no slash '/' at the end, please

# Cost of events, feel free to change them
cost_error = 2
cost_mutation = 3
cost_HGT = 5
cost_out_group_open = 7
cost_out_group_extend = 1


# And this is for a part of the simulation that generates inversions
sib_dir = '/home/bob/LLNL/sib'		# no slash '/' at the end, please

# Executable names, do not modify these unless you know what you're doing
sib = 'sib.py'
treeGen = 'treeGen'
snpGen = 'snpGenWithInversions'
hgtFinder = 'hgtWithBlocks'
myDraw = 'myDraw'
dumpSequence = 'dumpSequence'
compareHGT = 'compareHGT'

# Put master genome here, or leave it blank (master_strain_name = '')
master_strain_name = ''				

# Input file names
if simulation:
	tree_name = 'tree_c'
	fasta_file = 'fasta_c'
	SNP_all_matrix = 'matrix_c'
	SNP_all = 'snp_all_c'
else:
	# For real data, modify the input file names here
	tree_name = sys.argv[1]
	fasta_file = sys.argv[2]
	SNP_all = sys.argv[3]
	SNP_all_matrix = 'SNPs_all_matrix'

# A bunch of output file names
tree_out = 'tree_out_c'
snp_map = 'snp_map_c'
block_out = 'block_out_c'
event_out = 'event_out_c'
snp_out = 'snp_out_c'
hgt_only = 'hre_result_c'
event_summ = 'event_summ_c'
hgt_from_to = 'hre_from_to_c'
node_summ = 'node_summ_c'
hreSNP = 'hreSNPs'
tree_hre_label = 'tree_hre_labels.tre'
seq_out = 'seq_hre.fasta'
hre_from_to_by_locus = 'hre_from_to_by_locus'
tanglegram = 'tanglegram.eps'
picture = 'picture_c.png'
#seq_out = 'seq_out_c'

tree_name_int = 'tree_int'

# This is used in the picture. Modifying is not recommanded
font_file = "arial.ttf"


# The main part. It is fine to comment some steps.
def main():
        # This creates a SNP matrix from the SNPs_all file
	#os.system("my_bin/SNPs_all_2_matrix.pl SNPs_all SNPs_all_matrix")
	myRun ("%s/%s %s %s" % (my_bin, "SNPs_all_2_matrix.pl", SNP_all, SNP_all_matrix))

	if simulation:
		# This part generates a new evolutionary tree (for simulation)
		newTree()

		# This part generates symmetric inversion events (for simulation)
		genInversions()

		# This part generates SNPs with mutations, HGTs, and errors (for simulation)
		genSnpEvolution()

	# Check if user inputs the master genome name
	checkMasterStrainName()

	# This part runs the program to find HGTs (for real data or simulation)
	getHGT()

	# This part draw an RGB picture of mutations, errors, and HGTs
	drawPicture()

	# This part dumps the sequence of HGT to leaf nodes from given fasta input, replaced this with dum_hre_seqs.pl which gets both leaf and internal node seqs
	#dumpSeq()
	# This part dumps the sequence of all HRE regions extracted from fasta
	myRun ("%s/%s %s %s %s %s %s %s %s" % (my_bin, "dump_hre_seqs.pl", tree_out, fasta_file, event_summ, snp_map, node_summ, SNP_all, seq_out))

	# This prints out the SNPs involved in hre events, in SNPs_all format
	myRun ("%s/%s %s %s" % (my_bin, "hre_SNPs.pl", SNP_all, hreSNP))

	# This labes the nodes of a Newick tree file with the number of HRE events going each node
	myRun ("%s/%s %s %s %s" % (my_bin, "labelTree_HRE.pl", tree_out, node_summ, tree_hre_label))


	# This makes a table of donor (from) and recipient (to) for each SNP locus. For HRE's from/to internal nodes, all the strains under that node are listed
	myRun ("%s/%s %s" % (my_bin, "hre_from_to_leaf_table.pl", hre_from_to_by_locus))

	# This makes a tanglegram with lines connecting the taxa in which HRE's are predicted to have occurred. HRE's are from tree on left to tree on right.
	myRun ("%s/%s %s %s %s" % (my_bin, "hre_tanglegram.pl", tree_out,hgt_from_to,tanglegram ))

	if simulation:
		# This part compares the actual HGTs and detected HGTs (for simulation)
		compareResult()

	print "Done"

# It is fine to stop reading through this script at this point.

# Simulation file names
sib_internal_node = 'internal_nodes.sib'	# For simulation only
sib_external_node = 'external_nodes.sib'	# For simulation only
log_file = 'log_c'							# For simulation only
hgt_summary_file = 'hgt_summ_c'				# For simulation only

# Simulation Parameters
num_strain = 80
average_branch_length = 100
num_snps = 500
mutation_rate = 1				# percentage per branch length per SNP
HGT_rate = 1					# percentage per branch length per pair of nodes
error_rate = 0					# percentage
no_call_rate = 0				# percentage

# The following part is a copy of config file of symmetric
# inversion simulation. This program will generate a config file
# according to our setup.
rearrangement = 'rearrangements = {\n\
		"symmetric":90,\n\
		"intra_replichore":5,\n\
		"inter_replichore":5\n\
		}\n'

# size = num_snps

unbreakable = 'unbreakable_segments = {\n\
		"flag"      : True, # Set ''False'' if you do not want to use\n\
							# unbreakable segments. In this case, any pair\n\
							# of consecutive elements can be split by a\n\
							# symmetric reversal.\n\
		"mean"      : 3,\n\
		"deviation" : 10\n\
		}\n'

sign = 'sign = True\n'

symm_leng = 'symmetric_length = {\n\
		"flag"  : True, # Set "False" if you do not want to use a normal\n\
						# distribution. In these cases SIB will use a\n\
						# uniform distribution.\n\
		"mean"  : size/3,\n\
		"dev"   : size/5\n\
		}\n'

# tree = 

def myRun(cmd):
	print cmd
	os.system(cmd)

def writeConfig():
	tree_file = open(tree_name_int, "r")
	tree = tree_file.read()
	tree_file.close()
	out_config = "%s/config.py" % (sib_dir)
	outp = open(out_config, "w")
	outp.write (rearrangement)
	line = "size = %d\n" % (num_snps)
	outp.write (line)
	outp.write (unbreakable)
	outp.write (sign)
	outp.write (symm_leng)
	# trim if the last character is ';'
	if tree[-1] == '\n':
		tree = tree[:-1]
	if tree[-1] == ';':
		tree = tree[:-1]
	line = "tree = \"%s\"" % (tree)
	outp.write (line)
	outp.close()

# This part generates new evolutionary tree
def newTree():
	if os.path.exists(tree_name):
		myRun ("rm %s" % (tree_name))
	myRun ("%s/%s %d %d %s > %s" % (my_bin, treeGen, num_strain, average_branch_length, tree_name_int, tree_name))

# This part generates symmetric inversion events
def	genInversions():
	backup = "%s/old_config.py" % (sib_dir)
	if not os.path.exists(backup):
		myRun ("mv %s/config.py %s/old_config.py" % (sib_dir, sib_dir))
	writeConfig()
	myRun ("%s/%s" % (sib_dir, sib))

def genSnpEvolution():
	if os.path.exists(SNP_all_matrix):
		myRun ("rm %s" % (SNP_all_matrix))
	myRun ("%s/%s %s %s %s %d %f %f %f %f %s %s %s %s %f %f %f %f %f > %s" % (
			my_bin, snpGen, tree_name, sib_internal_node, sib_external_node, 
			num_snps, mutation_rate, HGT_rate, error_rate, no_call_rate, 
			log_file, fasta_file, SNP_all, hgt_summary_file,
			cost_error, cost_mutation, cost_HGT,
			cost_out_group_open, cost_out_group_extend,
			SNP_all_matrix))

def	checkMasterStrainName():
	if len(sys.argv) == 5:
		global master_strain_name
		master_strain_name = sys.argv[4]
		while master_strain_name[-1] == '\n' or master_strain_name[-1] == '\r':
			master_strain_name = master_strain_name[:-1]

# This part runs the program to find HGTs
def	getHGT():
	if os.path.exists(tree_out):
		myRun ("rm %s" % (tree_out))

	if os.path.exists(snp_map):
		myRun ("rm %s" % snp_map);

	if os.path.exists(block_out):
		myRun ("rm %s" % (block_out))

	if os.path.exists(event_out):
		myRun ("rm %s" % (event_out))

	if os.path.exists(snp_out):
		myRun ("rm %s" % (snp_out))

	if os.path.exists(event_summ):
		myRun ("rm %s" % (event_summ))

	if os.path.exists(hgt_from_to):
		myRun ("rm %s" % hgt_from_to)
	
	if os.path.exists(node_summ):
		myRun ("rm %s" % node_summ)

	myRun ("%s/%s %s %s %s %s %s %s %s %s %s %s %s %s %s %f %f %f %f %f %s" % (
		my_bin, hgtFinder, 
		tree_name, SNP_all_matrix, SNP_all, fasta_file, 
		tree_out, snp_map, block_out, event_out, snp_out, hgt_only, event_summ,
		hgt_from_to, node_summ,
		cost_error, cost_mutation, cost_HGT,
		cost_out_group_open, cost_out_group_extend, master_strain_name))

def drawPicture():
	if os.path.exists(picture):
		myRun ("rm %s" % (picture))
	myRun ("python %s/%s %s %s %s/%s" % (my_bin, myDraw, event_summ, picture, my_bin, font_file))

def dumpSeq():
	if os.path.exists(seq_out):
		myRun ("rm %s" % (seq_out))
	myRun ("python %s/%s %s %s %s" % (my_bin, dumpSequence, fasta_file, event_summ, seq_out))

def compareResult():
	myRun ("%s/%s %s %s" % (my_bin, compareHGT, hgt_summary_file, hgt_only))

if __name__ == '__main__':
	main()
