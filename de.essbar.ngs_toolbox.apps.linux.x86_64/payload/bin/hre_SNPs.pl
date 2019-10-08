#!/usr/bin/perl 

# hgt_SNPs.pl  $input_SNPs_file $SNPs_involved_in_HGTevents
# creates hgtSNPs has the SNP loci predicted by 
# HGTfinder to be involved in horizontal gene transfer.
# These are the SNPs in hgt_result_c

my $SNPs_file=$ARGV[0];
my $out=$ARGV[1];

open MAP,"snp_map_c" or die "Cannot open snp_map_c:$!\n";
my %all_loci_by_hgt_index=();
while (my $line=<MAP>) {
    chomp $line;
    my ($hgt_index,$locus_seq)=split/\t/,$line;
    $all_loci_by_hgt_index{$hgt_index}=$locus_seq;
}
close MAP;

open HGT_LOCI,"hre_result_c"  or die "Cannot open hre_result_c:$!\n";
my %hgt_snps=();
while (my $line=<HGT_LOCI>) {
    chomp $line;
    if ($line =~ /An\sHGT\sfrom/) {
	$line=<HGT_LOCI>;
	chomp $line;
	my @x=split/\s/,$line;
	foreach my $hgt_index (@x) {
	    $hgt_snps{$all_loci_by_hgt_index{$hgt_index}}=1;
	}
    }
}
close HGT_LOCI;


my %snps=(); 

open IN, "$SNPs_file" or die "Cannot open $SNPs_file: $!.\n";
open OUT,">$out" or warn "Cannot open $out: $!\n";
my $count=0;
my $locus_count=0;

$snp_len{$locus_count}=0;
my $prev_locus=-5;
my $locus_count=-1;
while (my $line = <IN>) {
    chomp($line);

    if ($line =~ /\d+\t(.*)\t(.*)\t(\d+(?:\sF|\sR)?|x)\t(.*)/) { 
	my $seq=$1;
	if ($prev_locus ne $seq ) {
	    $prev_locus = $seq;
	    $locus_count++;
	    if (defined $hgt_snps{$seq} && $hgt_snps{$seq} ==1 ) {
		print OUT "\n";
	    }
	}
	if (defined $hgt_snps{$seq} && $hgt_snps{$seq} ==1 ) {
	    print OUT "$line\n";
	}
    }
}

close OUT;
close IN;
