// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.QC.knime.nodes.FastQC;

import java.io.InputStream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

import com.genericworkflownodes.knime.config.INodeConfiguration;
//import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeDialog;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeFactory;
//import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;

import de.essbar.ngs_toolbox.QC.knime.nodes.FastQC.FastQCNodeModel;
import de.essbar.ngs_toolbox.QC.jsview.JSViewConfig;
import de.essbar.ngs_toolbox.QC.jsview.JSViewRepresentation;
import de.essbar.ngs_toolbox.QC.jsview.JSViewValue;
import de.essbar.ngs_toolbox.QC.knime.PluginActivator;

/**
    @brief FastQC Node Factory.
*/
public class FastQCNodeFactory extends GenericKnimeNodeFactory implements WizardNodeFactoryExtension<FastQCNodeModel, JSViewRepresentation, JSViewValue>{
    
    private static final NodeLogger logger = NodeLogger.getLogger(FastQCNodeFactory.class);
    private final JSViewConfig m_config = new JSViewConfig();;
    
    @Override
    public FastQCNodeModel createNodeModel() {
        INodeConfiguration tmpConfig = null;
        try {
            tmpConfig = getNodeConfiguration();
            return new FastQCNodeModel(tmpConfig, PluginActivator
                    .getInstance().getPluginConfiguration());
        } catch (Exception e) {
            logger.error("FastQC model instantiation failed", e);
        }
        return null;
        
    }

    @Override
    public GenericKnimeNodeDialog createNodeDialogPane() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new FastQCNodeDialog(tmpConfig);
        } catch (Exception e) {
            logger.error("FastQC dialog instantiation failed", e);
        }
        return null;
    }

    @Override
    protected InputStream getConfigAsStream() {
        return this.getClass().getResourceAsStream("config/config.xml");
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInteractiveViewName() {
        String name = m_config.getViewName();
        if (name == null || name.isEmpty()) {
            return super.getInteractiveViewName();
        }
        return name;
    }
}
