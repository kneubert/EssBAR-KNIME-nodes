// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.custom.GenericActivator;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.config.impl.PluginConfiguration;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;

public class PluginActivator extends GenericActivator {

    private static PluginActivator INSTANCE = null;   
    private static IPluginConfiguration PLUGIN_CONFIG = null;
    private static final List<ExternalTool> TOOLS = Arrays.asList(new ExternalTool("de.essbar.ngs_toolbox.apps", "Flexbar3", "flexbar.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "SPAdes", "spades.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Prokka", "prokka.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Parsnp", "parsnp"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Kraken", "kraken"), new ExternalTool("de.essbar.ngs_toolbox.apps", "KrakenReport", "kraken-report.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "KrakenSummary", "kraken_summary.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "ProkkaDB", "prokka_db.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Slimm", "slimm"), new ExternalTool("de.essbar.ngs_toolbox.apps", "SlimmDB", "slimm_db.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Slimm2Krona", "slimm2krona.py"),
    		new ExternalTool("de.essbar.ngs_toolbox.apps", "SRST2", "srst2.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "PubMLST", "pubMLST.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "stringMLST", "stringMLST_wrapper.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "contigMLST", "contigMLST.sh"),new ExternalTool("de.essbar.ngs_toolbox.apps", "MLVA", "MLVA_wrapper.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Kraken2", "kraken2.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Kraken2DB", "kraken2-build.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "FLASH2", "flash2"),new ExternalTool("de.essbar.ngs_toolbox.apps", "ABACAS", "abacas.sh"),new ExternalTool("de.essbar.ngs_toolbox.apps", "Pilon", "pilon.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "MIRA", "mira.py"), new ExternalTool("de.essbar.ngs_toolbox.apps", "kSNP3", "kSNP3_wrapper.sh"),
    		new ExternalTool("de.essbar.ngs_toolbox.apps", "Bowtie2", "bowtie2-2.3.5.sh"), new ExternalTool("de.essbar.ngs_toolbox.apps", "Bowtie2build", "bowtie2-2.3.5-build.sh"));

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        initializePlugin();
        INSTANCE = this;
    }

    public static PluginActivator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public List<ExternalTool> getTools() {
        return TOOLS;
    }
    
    @Override
    public IPluginConfiguration getPluginConfiguration() {
        if (PLUGIN_CONFIG == null) {
            // construct the plugin config
            PLUGIN_CONFIG = new PluginConfiguration("de.essbar.ngs_toolbox.apps", "NGS Applications", 
                PluginActivator.getInstance().getProperties(), getClass());
        }
        return PLUGIN_CONFIG;
    }
}
