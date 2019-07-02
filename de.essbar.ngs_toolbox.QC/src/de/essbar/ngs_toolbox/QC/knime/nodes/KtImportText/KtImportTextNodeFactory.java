// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.QC.knime.nodes.KtImportText;

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
import de.essbar.ngs_toolbox.QC.knime.nodes.KtImportTaxonomy.KtImportTaxonomyNodeModel;

/**
    @brief KtImportText Node Factory.
*/
public class KtImportTextNodeFactory extends GenericKnimeNodeFactory implements WizardNodeFactoryExtension<KtImportTextNodeModel, JSViewRepresentation, JSViewValue>{ 
    
    private static final NodeLogger logger = NodeLogger.getLogger(KtImportTextNodeFactory.class);
    private final JSViewConfig m_config = new JSViewConfig();;
    
    @Override
    public KtImportTextNodeModel createNodeModel() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new KtImportTextNodeModel(tmpConfig, PluginActivator
                    .getInstance().getPluginConfiguration());
        } catch (Exception e) {
            logger.error("KtImportText model instantiation failed", e);
        }
        return null;

    }

    @Override
    public GenericKnimeNodeDialog createNodeDialogPane() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new KtImportTextNodeDialog(tmpConfig);
        } catch (Exception e) {
            logger.error("KtImportText dialog instantiation failed", e);
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
