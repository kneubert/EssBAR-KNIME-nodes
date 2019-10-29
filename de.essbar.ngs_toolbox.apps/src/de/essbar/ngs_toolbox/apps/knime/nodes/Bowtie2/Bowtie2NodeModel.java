// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.Bowtie2;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief Bowtie2 Node Model.
*/
public class Bowtie2NodeModel extends GenericKnimeNodeModel {  
    protected Bowtie2NodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"bt2" ,"bt2l" }, {"fq" ,"fastq" ,"gz" ,"bz2" }, {"fq" ,"fastq" ,"gz" ,"bz2" }, {"fq" ,"fastq" ,"gz" ,"bz2" }, {"fq" ,"fastq" ,"gz" ,"bz2" },}, new String[][]{{"sam" },});
    }
}
