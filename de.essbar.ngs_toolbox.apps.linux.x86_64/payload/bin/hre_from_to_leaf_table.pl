#!/usr/bin/perl 

# hre_from_to_leaf_table.pl $out
# makes a table of all loci predicted to be involved in HRE and the donor (from) and recipient (to) strains.
# For HRE's from or to internal nodes, all the genomes under that node are listed in from/to columns

use Bio::TreeIO;

my $out=$ARGV[0];

open MAP,"snp_map_c" or die "Cannot open snp_map_c:$!\n";
my %all_loci_by_hgt_index=();
while (my $line=<MAP>) {
    chomp $line;
    my ($hgt_index,$locus_seq)=split/\t/,$line;
    $all_loci_by_hgt_index{$hgt_index}=$locus_seq;
}
close MAP;

my $treeio = new Bio::TreeIO( -file => "tree_out_c",
			      -format => "newick");
my $tree = $treeio->next_tree;
#print $tree;
#print $tree->find_node(-id =>'1');
#@nodes=$tree->get_nodes();
#foreach $node (@nodes) {
#    print "$node\n",$node->id,"\n";
#}
#    my @leaves=$node->get_all_Descendents;
#    foreach my $leaf (@leaves) {
#	print $leaf->id,"\n";
#    }
#}
open NODE,"node_summ_c";
my %nodeNum2Name=();
while (my $line=<NODE>){
    my @a=split/\t/,$line;
    
    my $node_num=$a[0];
    my $node_name=$a[1];
    #print "$node_num\t$node_name\n";
    if ($node_name =~ /\D+/) {
	# do nothing
    } else {
	$node_name ="Node_".$node_num;
    }
    $nodeNum2Name{$node_num}=$node_name;
    
}
if (!defined $nodeNum2Name{0}) {
    $nodeNum2Name{0}="root";
}
my @node_nums=sort {$b <=> $a} keys %nodeNum2Name;
my $outside_num=$node_nums[0]+1;
$nodeNum2Name{$outside_num}="outside";
print "outside node=$outside_num\n";
my %already=();
foreach my $num (sort keys %nodeNum2Name) {
    print "$num\t$nodeNum2Name{$num}\n";
}

open OUT,">$out" or die "Cannot open $out for writing: $!\n";;

open HGT_LOCI,"hre_result_c"  or die "Cannot open hre_result_c for reading:$!\n";
my %hgt_snps=();
print OUT "from\tto\tlocus\n";
while (my $line=<HGT_LOCI>) {
    chomp $line;
    if ($line =~ /An\sHGT\sfrom\snode\s(\d+)\sto\snode\s(\d+)/ || $line =~ /An\sHGT\sfrom\s(outside)\sto\snode\s(\d+)/) {
	$from_node_name=$nodeNum2Name{$1};
	$to_node_name=$nodeNum2Name{$2};
	if ($1 eq "outside" ) {
	    $from_node_name="outside";
	}
	#print "\$from_node_name: $from_node_name\n";
	#print "\$to_node_name: $to_node_name\n";
	if ($from_node_name ne "root" && $from_node_name ne "outside" ) {
	    $from_node=$tree->find_node(-id => "$from_node_name");
	} else {
	    $from_node=$from_node_name;
	}
	if ($to_node_name ne "root" && $to_node_name ne "outside" ) {
	    $to_node=$tree->find_node(-id => "$to_node_name");
	} else {
	    $to_node=$to_node_name;
	}
	#print "\$from_node: $from_node\n";
	#print "\$to_node: $to_node\n";
	my @leaves_from=();
	my @leaves_to=();
	if ($from_node eq "root" || $from_node eq "outside" ) {
	    @leaves_from=($from_node);
	} elsif ( defined $from_node &&  $from_node->is_Leaf) {
	    @leaves_from=($from_node->id);
	} else  {
	    if (defined $from_node) {
		my @tmp=$from_node->get_all_Descendents;
		foreach my $node (@tmp) {
		    if ($node->is_Leaf) {
			push @leaves_from,$node->id;
		    }
		}
	    }
	}
	if ($to_node eq "root" || $to_node eq "outside" ) {
	    @leaves_to=($to_node);
	} elsif (  defined $to_node && $to_node->is_Leaf) {
	    @leaves_to=($to_node->id);
	} else  {
	    if (defined $to_node) {
		my @tmp=$to_node->get_all_Descendents;
		foreach my $node (@tmp) {
		    if ($node->is_Leaf) {
			push @leaves_to,$node->id;
		    }
		}
	    }
	}

	$line=<HGT_LOCI>;
	chomp $line;
	my @x=split/\s+/,$line;
	foreach my $hgt_index (@x) {
	    if ($hgt_index =~ /\d/) {
	
		foreach my $leaf_from (@leaves_from) {
		    foreach my $leaf_to (@leaves_to) {
			$line2print=$leaf_from.".".$leaf_to.".".$all_loci_by_hgt_index{$hgt_index};
			if (!defined $already{$line2print} ) {
			    print OUT "$leaf_from\t$leaf_to\t$all_loci_by_hgt_index{$hgt_index}\n";
			   $already{$line2print}=1;
			}
		    }
		}
	    }
	}
    }
}
close HGT_LOCI;
close OUT;



