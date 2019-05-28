// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.QC.knime.nodes.KtImportTaxonomy;

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

/**
    @brief KtImportTaxonomy Node Factory.
*/
public class KtImportTaxonomyNodeFactory extends GenericKnimeNodeFactory implements WizardNodeFactoryExtension<KtImportTaxonomyNodeModel, JSViewRepresentation, JSViewValue>{
    
    private static final NodeLogger logger = NodeLogger.getLogger(KtImportTaxonomyNodeFactory.class);
    private final JSViewConfig m_config = new JSViewConfig();;
    
    @Override
    public KtImportTaxonomyNodeModel createNodeModel() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new KtImportTaxonomyNodeModel(tmpConfig, PluginActivator
                    .getInstance().getPluginConfiguration());
        } catch (Exception e) {
            logger.error("KtImportTaxonomy model instantiation failed", e);
        }
        return null;

    }

    @Override
    public GenericKnimeNodeDialog createNodeDialogPane() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            return new KtImportTaxonomyNodeDialog(tmpConfig);
        } catch (Exception e) {
            logger.error("KtImportTaxonomy dialog instantiation failed", e);
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
