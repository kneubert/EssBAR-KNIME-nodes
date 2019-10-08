#!/usr/bin/perl 
#  labelTree_HRE.pl input_tree node_summ_c   tree_hre_labels

use Bio::TreeIO;

my $intree=shift;  
#print "intree: $intree\n";
my $countFile=shift;
my $outfile=shift;
my $outfile2=$outfile.".NodeNums.tre";
my %nh=();
my %nh_NodeNum;
open IN,"$countFile";
my @lines=<IN>;
close IN;
chomp @lines;
foreach my $line (@lines) {
    chomp $line;
    my @a=split/\t/,$line;

    my $node=$a[0];
    my $name=$a[1];
    chomp $node; 
    chomp $name;
    $m=$a[3];
    $e=$a[4];
    $hgt2here=$a[5];
    my $node_num=$node;
    if ($name =~ /\S+/) { 
	$node=$name; 
    }
    
    if ($m == 0 ) { 
	$mut=$e;
    } else { 
	$mut=$m;
    }
    
    #print " node: $node name: $name  m: $m  e: $e  hgt2here: $hgt2here \n";
    #$nh{$node}="x".$hgt2here."_m".$mut;
    $nh{$node}=$hgt2here;
    $nh_NodeNum{$node}="N".$node_num."_x".$hgt2here;
    #print "$node\n";
    
}


my $treeio = new Bio::TreeIO(-file   => "$intree",
                            -format => "newick");

my $tree = $treeio->next_tree;
@nodes=$tree->get_nodes();
foreach $node (@nodes) {
    my $oldid=$node->id;
   
    $oldid =~ s/Node_//;
 #   print "$oldid\n";
    if (!defined $nh{$oldid}) {
	$nh{$oldid}=0;
    }
    my $newid="";

    if ($node->is_Leaf) {
	$newid=$oldid."_".$nh{$oldid};
    } else {
	$newid=$nh{$oldid};
    }
   # print "old id: $oldid\n";
   # print "new id: $newid\n";
    $node->id($newid);
    
}
my $treeio_nodes_unnamed = new Bio::TreeIO( -file => ">$outfile",
                                       -format => "newick");


$treeio_nodes_unnamed->write_tree($tree);


###########

$treeio2 = new Bio::TreeIO(-file   => "$intree",
			  -format => "newick");

my $tree = $treeio2->next_tree;
@nodes=$tree->get_nodes();
foreach $node (@nodes) {
    my $oldid=$node->id;
    $oldid =~ s/Node_//;
    if (!defined $nh_NodeNum{$oldid}) {
	$nh_NodeNum{$oldid}=0;
    }
    my $newid="";

    if ($node->is_Leaf) {
	$newid=$oldid."_".$nh_NodeNum{$oldid};

    } else {
	$newid=$nh_NodeNum{$oldid};
    }
   # print "old id: $oldid\n";
   # print "new id: $newid\n";
    $node->id($newid);
    
}
$treeio_nodes_named = new Bio::TreeIO( -file => ">$outfile2",
                                       -format => "newick");


$treeio_nodes_named->write_tree($tree);



##################

exit;

