// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.contigMLST;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief contigMLST Node Model.
*/
public class contigMLSTNodeModel extends GenericKnimeNodeModel {  
    protected contigMLSTNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"*" }, {"*" }, {"fa" ,"fasta" ,"gbk" ,"embl" ,"gz" },}, new String[][]{{"txt" ,"tsv" ,"csv" }, {"json" }, {"fa" ,"fasta" },});
    }
}
