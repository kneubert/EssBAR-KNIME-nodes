#!/usr/bin/perl 

#dump_hre_seqs.pl  tree_out_c fastainput event_summ_c snp_map_c node_summ_c SNPs_all seq_hre.fasta
use Bio::TreeIO;
my $intree=shift;  
my $fasta=shift;
my $eventfile=shift;
my $snp_map=shift;
my $node_summ=shift;
my $snps_all=shift;
my $outfile=shift;

my %locus_by_hre_snp_num=(); #  $locus_by_hre_snp_num{$hre_snp_num}=$locus_seq;
my %strain=&read_fasta_input($fasta);
my %fors=(); # $fors{$locus_seq}{$id}{$position} = $snp_char;
my $halfk;
my %nodeNames=(); # $nodeNames{$nodeNum}=$nodeName;

open IN,"$snp_map";
while (my $line=<IN>) {
    chomp $line;
    my ($hre_snp_num,$locus_seq)=split/\t/,$line;
    $locus_by_hre_snp_num{$hre_snp_num}=$locus_seq;
}
close IN;

open IN,"$node_summ";
while (my $line=<IN>) {
    chomp $line;
    my ($nodeNum,$nodeName,@junk)=split/\t/,$line;
    if ($nodeName !~ /\S/) {
	$nodeName=$nodeNum;
    }
    $nodeNames{$nodeNum}=$nodeName;
}
close IN;

&read_snps_from_SNPs_all_file($snps_all);  # sets %fors


my $treeio = new Bio::TreeIO(-file   => "$intree",
                            -format => "newick");

my $tree = $treeio->next_tree;
@nodes=$tree->get_nodes();

#print @nodes;

open IN,"$eventfile";
open OUT,">$outfile";
while (my $line=<IN> ){
	#print "$line\n";
    if ($line=~/^X\s(.*)/) {
#	print "$line";
	chomp $line;
	my %region=();
	@snp_ids=split/\s/,$1;
	my $sourceNodeNum=shift @snp_ids ;
	my $destinationNodeNum=shift @snp_ids;
       	my $sourceNodeID="Node_".$sourceNodeNum;
	my $destinationNodeID="Node_".$destinationNodeNum;
#	print "\$sourceNodeID: $sourceNodeID\n";
#	print "\$destinationNodeID: $destinationNodeID\n";
	$sourceNodeName=$nodeNames{$sourceNodeNum};


	$destinationNode= $tree->find_node(-id=>$destinationNodeID);
	my @destination_leaves=();
#	print $destinationNode->is_leaf();

	if (defined $destinationNode) {
	    my @children=$destinationNode->get_all_Descendents;
	    for my $child (@children){ 
		push @destination_leaves,$child->id;
	    }
#	    print "\$destinationNodeID=$destinationNodeID:\nleaves=@destination_leaves\n";
	} else {  # 	if (defined $destinationNode) {
	    #print "$destinationNodeID is a leaf\n";
	    $destinationNodeID=$nodeNames{$destinationNodeNum};
	    push @destination_leaves,$destinationNodeID;
	    #print "@destination_leaves\n";
	} #  else {  # 	if (defined $destinationNode) {
	#print "\@snp_ids: @snp_ids\n";
	foreach my $hre_snp_num (@snp_ids) {
	    $locus_seq= $locus_by_hre_snp_num{$hre_snp_num};
	    foreach my $id (@destination_leaves) {
		foreach my $pos (keys %{$fors{$locus_seq}{$id}}) {
		    #print "$hre_snp_num $locus_seq $id pos: $pos\n";
		    ($snp_pos,$strand) = split/\s/,$pos;
		    $region{$id}{$snp_pos}=1;
		}
	    }
	}
	
	foreach my $id (keys %region) {
	   
	    my @sorted_region= sort {$a <=> $b} keys %{$region{$id}};
	    #print "sorted region: @sorted_region\n";
	    $snp_positions=join(",",@sorted_region);
	    my $start_position=$sorted_region[0]-$halfk;
	    
	    my $end_position=$sorted_region[scalar(@sorted_region)-1]+$halfk;
	    my $len=$end_position-$start_position+1;
	    my $seq=substr($strain{$id},$start_position-1,$len);
	    print OUT ">$id ".$start_position."-".$end_position." $line sourceNode=$sourceNodeID destinationNodeID=$destinationNodeID SNPpositions=$snp_positions\n$seq\n";
	}
    }
}
close OUT;


sub read_fasta_input {
    my $infile=shift;
    open INDATA,"$infile" or die "Can't open $infile: $!\n";
        
    #get all sequences and make a hash called strain with id and sequence info
    my $sequence = "";
    my $id;
    my %strain=();
    while (my $line = <INDATA>) {
         if ($line =~ /^>(\S+)/) {
            if ($sequence ne "") {
                $ids[$num_genomes]=$id;
                $strain{$id}=uc($sequence); 
                $num_genomes++;
            }
            $id = $1;
           chomp($id);
            $sequence = "";
        } else {
            chomp($line);
             $line =~ s/[\n\t\s\r]//g; 
             $sequence .= $line;
        }
        
    }   
    
    # Add last one
    if (defined $id) {
        $ids[$num_genomes]=$id;
        $strain{$id}=uc($sequence);
        $num_genomes++;
    }
    
    close INDATA or warn  "Can't close $infile: $!\n";
   return %strain;

} # end sub read_fasta_input 


sub read_snps_from_SNPs_all_file {
    my  $all_snps_file=shift;
    print "$all_snps_file\n";
    open ALL_SNPS, "$all_snps_file" ;
    my $primer="X";
    while (my $line = <ALL_SNPS>){

        if ($line =~ /^\d+\t(.*)\t(.*)\t(\d+(?:\sF|\sR)?|x)\t(.*)/ ) {
            
            $primer = $1;
            my $snp_char = $2;
            my $position = $3;
            my $name= $4;
            chomp($name);
	    $halfk=(length($primer)-1)/2;
            $fors{$primer}{$name}{$position} = $snp_char;   # global, but gets reset
       
        }
    }
    
} # sub read_snps_from_SNPs_all_file {


