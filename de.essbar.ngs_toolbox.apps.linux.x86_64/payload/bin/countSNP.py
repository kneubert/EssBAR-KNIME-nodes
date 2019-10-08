#!/usr/bin/env python

import sys

def printUsage():
	print "Usage"
	print "$ %s <event summary file>" % (sys.argv[0])
	quit()

def main():
	if len(sys.argv) != 2:
		printUsage()

	try:
		f = open (sys.argv[1], 'r')
	except IOError as e:
		print ("({})".format(e))
		quit()
	
	f.readline()
	f.readline()
	total = sum(map(int, f.readline().split()))
	print "The total number of SNPs counting duplicated blocks: %d" % total

if __name__ == '__main__':
	main()
