// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.Kraken;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
    @brief Kraken Node Model.
*/
public class KrakenNodeModel extends GenericKnimeNodeModel {  
    protected KrakenNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fasta" ,"fa" ,"fastq" ,"fq" ,"gz" ,"bz2" },{"*" },}, new String[][]{{"kraken"}, {"kraken"}, {"kraken" },});
    }
}
