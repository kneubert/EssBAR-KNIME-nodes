// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.stringMLST;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief stringMLST Node Model.
*/
public class stringMLSTNodeModel extends GenericKnimeNodeModel {  
    protected stringMLSTNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fq" ,"fastq", "gz" }, {"fq" ,"fastq", "gz" }, {"txt" },}, new String[][]{{"txt" ,"tsv" ,"csv" }, {"fq" ,"fastq" },});
    }
}
