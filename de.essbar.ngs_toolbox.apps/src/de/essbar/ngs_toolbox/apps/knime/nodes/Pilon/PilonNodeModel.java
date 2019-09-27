// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.Pilon;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief Pilon Node Model.
*/
public class PilonNodeModel extends GenericKnimeNodeModel {  
    protected PilonNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fa" ,"fasta" }, {"fq" ,"fastq" ,"gz" ,"bz2" },}, new String[][]{{"fa" ,"fasta" }, {"*","inactive"}});
    }
}
