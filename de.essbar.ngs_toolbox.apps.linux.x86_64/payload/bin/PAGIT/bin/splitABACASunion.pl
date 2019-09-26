#! /usr/bin/perl -w
#
# File: split2Multifasta.pl
# Time-stamp: <26-Sep-2011 15:20:28 tdo>
# $Id: $
#
# Copyright (C) 2012 by Pathogene Group, Wellcome Trust Sanger Institute
#
# Author: Thomas Dan Otto
#
# Description: This program should be run after ABACAS, if the reference got processed with joinMultifasta.pl

use strict;

if (scalar(@ARGV)<5) {
  die "usage: <Reference File> <Union of reference (output of joinMultifasta)> <ordered ABACAS result file> <abacas crunchfile (end with crunch)> <abacas tab file ending with tab> optional <resultname prefix>\n\nThis script will split the query file accordinly to the reference, assuming the reference got processed with joinMultifasta.pl.\n";
}

my $spacer;
my $reforg=shift;
my $refunion = shift;
my $queryFile = shift;
my $crunchFile = shift;
my $gff = shift;
my $result=shift;

if (!defined($result)){
	$result="Split";	
}

my $splitfile=$refunion.".splitinfo.txt";


my $ref_splitborder	= getSplitinfo($splitfile);
my $seq				= getFasta($queryFile);
my $ref_embl		= getembl($gff);
my $ref_crunch		= getCrunch($crunchFile);

my $resSeq='';
my $lastEnd=length($seq);
### start from behind
for my $id (sort {$b <=> $a} keys %$ref_splitborder){
#	print "Split $id $$ref_splitborder{$id}\n";
	my ($name,$end) = $$ref_splitborder{$id} =~ /^(\S+)\t(\d+)$/;
	my ($start)=findStart($id,$ref_crunch,$ref_embl);
#	print "Cut from $start - $lastEnd\n";

	### Split the contigs files description
	splitEMBL($start,$lastEnd,$result,$ref_embl,$name);
	
	### Get the sequence
	my $resSeqtmp=">$name\n".substr($seq,($start-1),($lastEnd-$start+1))."\n";
	### delete the N's at the end
	$resSeqtmp=~ s/N+$//g;
	$resSeq=$resSeqtmp.$resSeq;
	$lastEnd=($start-1);
	
}


### write the sequence file
open F, "> $result.ABACAS.fasta" or die "Couldn't write the file $result.ABACAS.fasta";
print F $resSeq;
close(F);

#### functions

sub splitEMBL{
	my $start = shift;
	my $end   = shift;
	my $result= shift;
	my $ref_embl=shift;
	my $name  = shift;
	
	my$res;
	foreach my $pos (sort {$a<=>$b} keys %$ref_embl){
		my $featureEnd=$$ref_embl{$pos}[0];
		if ($pos >= $start && $pos <= $end){
			my $newStart=($pos-$start+1);
			my $newEnd=($featureEnd-$start+1);
			if (defined($$ref_embl{$pos}[2])){
			$res.= "FT   contig          complement($newStart..$newEnd)\n";
			} else {
				$res.="FT   contig          $newStart..$newEnd\n";	
			}
			$res.=$$ref_embl{$pos}[1];
		}
		
	}	
	if (defined($res)){
		open F, ">$result.$name.features.tab" or die "Couldn't generated file $result.$name.features.tab: $!\n";
		print F $res;
		close(F);
	}
}

sub findStart{
	my $posRef=shift;
	my $ref_crunch=shift;	
	my $ref_tab=shift;
	
	### first find the first mummer hit
	my $i=($posRef-200);
	while(!defined($$ref_crunch{$i})){
		$i++;	
	}
	
	my $j=($$ref_crunch{$i}+20);
	### second, find the first feature with it
	while (!defined($$ref_tab{$j})){
		$j--;
	}
	
	return $j;	
}

sub getCrunch{
	open F, shift or die "Sorry, cannot find the crunch file, that was produced by abacas: $1\n";	
	my %h;	
	while (<F>){
		# 66 98 1 16361 NODE_279_length_16301_cov_13.824551 12752 29112 unknown NONE
		my @ar=split(/\s+/);
		$h{$ar[5]}=$ar[2];
	}
	return \%h;	
}

sub getembl{
	open F, shift or die "Sorry, cannot find the splitinfo file, that was produced by joinMultifasta: $1\n";	
	my %h;
	my $n;
	
	### get rid of the ID tag
	$_=<F>;
	while (<F>){
		chomp;
		if (/^FT\s+\S+\s+complement\((\d+)\.\.(\d+)\)/ || /^FT\s+\S+\s+(\d+)\.\.(\d+)$/){
			$n=$1;
			$h{$n}[0]=$2;
			if (/complement/){
				$h{$n}[2]=1;	
			}
			
		}
		else {
			$h{$n}[1].=$_."\n";	
		}
	}
	return \%h;
}


sub getSplitinfo{
	open F, shift or die "Sorry, cannot find the splitinfo file, that was produced by joinMultifasta: $1\n";	
	my %h;
	while (<F>){
		my @ar=split(/\t/);
		$h{$ar[1]}="$ar[0]\t$ar[2]";
	}
	return \%h;
}

sub getFasta{
  my $file = shift;

  my $seq;
  
  open (F,$file) or
        die "Couldn't open Sequence file $file: $!\n";
  
  while (<F>) {
  	if (!/^>/){
          chomp;
          $seq.=$_
  	}
  }
  return $seq
}