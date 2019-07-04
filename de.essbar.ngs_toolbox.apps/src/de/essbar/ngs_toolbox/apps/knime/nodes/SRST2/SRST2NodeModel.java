// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.SRST2;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief SRST2 Node Model.
*/
public class SRST2NodeModel extends GenericKnimeNodeModel {  
    protected SRST2NodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fastq" ,"fq" ,"gz" }, {"fastq" ,"fq" ,"gz" }, {"fasta" ,"fa" }, {"txt" }, {"fasta" ,"fa" },}, new String[][]{{"*", "inactive"}});
    }
}
