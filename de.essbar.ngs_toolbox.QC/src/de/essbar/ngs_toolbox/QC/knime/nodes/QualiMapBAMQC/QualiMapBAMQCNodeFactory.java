// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.QC.knime.nodes.QualiMapBAMQC;

import java.io.InputStream;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeDialog;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeFactory;

import de.essbar.ngs_toolbox.QC.jsview.JSViewConfig;
import de.essbar.ngs_toolbox.QC.jsview.JSViewRepresentation;
import de.essbar.ngs_toolbox.QC.jsview.JSViewValue;
import de.essbar.ngs_toolbox.QC.knime.PluginActivator;
import de.essbar.ngs_toolbox.QC.knime.nodes.QUAST.QUASTNodeModel;

/**
    @brief QualiMapBAMQC Node Factory.
*/
public class QualiMapBAMQCNodeFactory extends GenericKnimeNodeFactory implements WizardNodeFactoryExtension<QualiMapBAMQCNodeModel, JSViewRepresentation, JSViewValue>{
    
    private static final NodeLogger logger = NodeLogger.getLogger(QualiMapBAMQCNodeFactory.class);
    private final JSViewConfig m_config = new JSViewConfig();;
    
    @Override
    public QualiMapBAMQCNodeModel createNodeModel() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new QualiMapBAMQCNodeModel(tmpConfig, PluginActivator
                    .getInstance().getPluginConfiguration());
        } catch (Exception e) {
            logger.error("QualiMapBAMQC model instantiation failed", e);
        }
        return null;

    }

    @Override
    public GenericKnimeNodeDialog createNodeDialogPane() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new QualiMapBAMQCNodeDialog(tmpConfig);
        } catch (Exception e) {
            logger.error("QualiMapBAMQC dialog instantiation failed", e);
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
