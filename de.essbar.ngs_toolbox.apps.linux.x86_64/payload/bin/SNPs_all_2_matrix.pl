#!/usr/bin/perl 

# SNPs_all_2_matrix.pl SNPs_all  SNPs_all_matrix

my $snp_file=$ARGV[0];
my $out=$ARGV[1];

open OUT,">$out";

open IN, "$snp_file" or die "Cannot open $snp_file: $!.\n";

my $count_of_positions=0;
my %snp_len=();
$snp_len{$count_of_positions}=0;
my $previous_locus=-1;
my %by_id=();

while (my $line = <IN>) {
    chomp($line);

   if ($line =~ /(\d+)\t.*\t(.*)\t(\d+(?:\sF|\sR)?|x)\t(.*)/) { 
	my $locus=$1;
	my $snp=$2;
	my $positionAndStrand=$3;
	my $id=$4;
 	#print "locus:$locus\n";
	#print "allele: $snp\n";
	#print "position and strand: $positionAndStrand\n";
	#print "id: $id\n";
	if ($previous_locus != $locus) {
            $count_of_positions++;
	    $snp_len{$count_of_positions}=0;
            $previous_locus = $locus;
        }

	$by_id{$id}{$count_of_positions}=$snp;
	if ($snp_len{$count_of_positions} < length($snp)) { $snp_len{$count_of_positions} = length($snp); }
    }
}

my $seqlen=0;
foreach my $count_of_positions (keys %snp_len) {
    $seqlen +=$snp_len{$count_of_positions};
} 

my $num_ids= scalar(keys %by_id);

print OUT "$num_ids   $seqlen\n";

foreach my $id (sort {$a cmp $b} keys %by_id) {
 
    my $name=$id;  
    $name =~ s/^(\S+)\s.*/$1/;
    my  $name2 =substr($name,0,88);

  
    printf OUT "%-90.90s",$name2;
   
   foreach my $count_of_positions (sort {$a <=> $b} keys %snp_len ) { 
       my $snp="";
       if (defined $by_id{$id}{$count_of_positions} && $by_id{$id}{$count_of_positions} ne "x") {
	   $snp=$by_id{$id}{$count_of_positions};
	   if (length($snp) < $snp_len{$count_of_positions}) { 
	       my $num_to_add=$snp_len{$count_of_positions} - length($snp);
	       $snp .= "-" x $num_to_add;
	   }
       } else {
	   $snp = "N" x $snp_len{$count_of_positions};
       }

       print OUT "$snp";
   }
   print OUT "\n";
}

close OUT;
