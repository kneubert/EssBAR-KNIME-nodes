#!/usr/bin/perl 

# hre_tanglegram.pl tree_out_c  hre_from_to_c out.eps
# hre_tanglegram.pl tree_out_c  hre_from_to_c out.eps 300
# hre_tanglegram.pl tree_out_c  hre_from_to_c out.eps 10 500


use Bio::Tree::Draw::Cladogram;
use Bio::TreeIO;
use Bio::Tree::Tree;
use Tree;


my $treefile1=$ARGV[0];
my $hre_from_to_c=$ARGV[1];
my $outplot=$ARGV[2];
my $column=$ARGV[3];
my $right_margin=$ARGV[4];
if (!defined $column ) {
    $column=10;
}
if (!defined $right_margin ) {
    $right_margin=$column*3;
}



open IN,"$hre_from_to_c";
my %hres=();
my $total_hre=0;
while (my $line=<IN>) {
    chomp $line;
    my ($from,$from_name,$to,$to_name,$hre)=split/\t/,$line;
    if ($from ne "From"){
	if ($from_name =~ /\D+/) {
	    #nothing
	} else {
	    $from_name ="Node_".$from;
	}
	if ($to_name =~ /\D+/) {
	    #nothing
	} else {
	    $to_name ="Node_".$to;
	}

	$hres{$from_name}{$to_name}=$hre;
	$total_hre +=$hre;

    }
}
close IN;

`sed -e 's/;/9);/' $treefile1 | sed -e 's/^(/(outside:1,(/' > tree.outside`;



my $treeio = new Bio::TreeIO(-file   => "tree.outside",
                           -format => "newick");

my $tree = $treeio->next_tree;
my $treeio2 = new Bio::TreeIO(-file   => "tree.outside",
                           -format => "newick");
my $tree2=$treeio2->next_tree;


#Each correspondence between a $taxon1 of the first tree and a $taxon2 of the second tree is established by setting 
#$taxon1->add_tag_value('connection',$taxon2);
#The branch from the parent to a child $node, as well as the child label, can be colored 
#$node->add_tag_value('Rcolor',$r); $r=0-1 for intensity

my @nodes= $tree->get_nodes();
foreach $node (@nodes) {
    #print $node->id(),"\n";
    $node->remove_all_tags();
}
@nodes2= $tree2->get_nodes();
foreach my $node (@nodes2) {
    #print "nid: ", $node->id(), "\n";
    my $nid= $node->id();
    #print "\$nid: $nid\n";
    $nid = "_".$nid;
    $node->id($nid);
#    $node->remove_all_tags();
}

foreach $from_name (keys %hres) {
	@from=$tree->find_node(-id=>$from_name);
	if (defined $from[0]) {
	    $from[0]->remove_tag('connection');
	}
}


foreach $from_name (keys %hres) {
    foreach $to_name (keys %{$hres{$from_name}}) {
	print "to_name: $to_name\tfrom_name: $from_name\n";
	$to_nameZ="_".$to_name;
	@from=$tree->find_node(-id=>$from_name);
	@to=$tree2->find_node(-id=>$to_nameZ);
	#print "from @from\nto @to\n";
	#print "from: ",$from[0]->id(),"\n";
	#print "to: ",$to[0]->id(),"\n";

	my $fraction_hres_in_tree=$hres{$from_name}{$to_name}/$total_hre;
	#if ($fraction_hres_in_tree > .01) {
	    if (defined $from[0] && defined $to[0]) {
		$from[0]->add_tag_value('connection',$to[0]);
		$from[0]->add_tag_value('blue',$fraction_hres_in_tree);  #$r); $r=0-1 for intensity. This isn't correct, nothing gets colored.
	    }
	#}
    }
}


my $dist_to_leaf= $tree->total_branch_length;      # $tree->max_distance_to_leaf;
#print "\$dist_to_leaf: $dist_to_leaf\n";



my  $obj=Bio::Tree::Draw::Cladogram->new(-tree=>$tree,
					 -second=>$tree2,
					 -colors=>1,
					 -compact=>1,      # 0 is with branch lengths, 1 is without
					 -right=>$dist_to_leaf*20,  
					 -column=>$dist_to_leaf*200); 


$obj->print(-file=>$outplot);

# error with eps rendering, so must do this to get whole plot to show 
`eps2eps $outplot $outplot.2`;
`mv $outplot.2 $outplot`;

`rm tree.outside`;
