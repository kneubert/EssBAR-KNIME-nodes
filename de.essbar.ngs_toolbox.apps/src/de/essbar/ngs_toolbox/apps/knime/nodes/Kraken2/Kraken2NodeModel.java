// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.Kraken2;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief Kraken Node Model.
*/
public class Kraken2NodeModel extends GenericKnimeNodeModel {  
    protected Kraken2NodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fasta" ,"fa" ,"fastq" ,"fq" ,"gz" ,"bz2" }, {"k2d" }}, new String[][]{{"kraken" }, {"kreport" }, {"fasta" ,"fa" ,"fastq" ,"fq" }, {"fasta" ,"fa" ,"fastq" ,"fq" },});
    }
}
