// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.QC.knime.nodes.MultiQC;

import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;
import com.genericworkflownodes.knime.parameter.Parameter;

import de.essbar.ngs_toolbox.QC.jsview.JSViewRepresentation;
import de.essbar.ngs_toolbox.QC.jsview.JSViewValue;
import de.essbar.ngs_toolbox.QC.jsview.JSViewConfig;
import de.essbar.ngs_toolbox.QC.jsview.JSViewHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JavaScriptViewCreator;
import org.knime.js.core.JSONDataTable.JSONDataTableRow;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;


/**
    @brief multiQC Node Model.
*/
public class MultiQCNodeModel extends GenericKnimeNodeModel implements WizardNode<JSViewRepresentation, JSViewValue>{  
	
	private static final NodeLogger LOGGER = NodeLogger.getLogger(MultiQCNodeModel.class);
    private final Object m_lock = new Object();
    private final JSViewConfig m_config;
    private JSViewRepresentation m_representation;
    private JSViewValue m_viewValue;
    private final JavaScriptViewCreator<JSViewRepresentation, JSViewValue> m_viewCreator;
    private String m_viewPath;
    
    private Long m_optionalViewWaitTime = null;
    
    private static final int BUFFER_SIZE = 2048;

    /**
     * The file content loaded from the file.
     */
    private String m_data;
	
    protected MultiQCNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{""},{""},{""},{""}}, new String[][]{{"*" ,"inactive" },});
        
        m_config = new JSViewConfig();
        m_viewCreator = new JavaScriptViewCreator<>(getJavascriptObjectID()); 
        m_representation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
    }
    
    
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
    	PortObjectSpec[] outportspec= super.configure(inSpecs);
    	
		m_config.loadDefaults();
    	
        if (StringUtils.isEmpty(m_config.getJsCode())) {
            throw new InvalidSettingsException("No script defined");
        }
        
        if (StringUtils.isEmpty(m_config.getJsCode())) {
            throw new InvalidSettingsException("No script defined");
        }
       
        return outportspec;
    }
    
    @Override
    protected PortObject[] execute(PortObject[] inObjects,
            ExecutionContext execContext) throws Exception {
    	
    	PortObject[] outports = super.execute(inObjects, execContext);
 	
    	String output = "";
        for (Parameter<?> param : super.getNodeConfiguration().getParameters()) {
        	System.out.println(param.getKey() + ":" + param.getValue());
        	if (param.getKey().matches("filename")) {
        		output = param.getValue().toString();
        	}
        
        }
        File file = new File(output);
        String file_name = file.getName().toString();
        File html_file;
        
        System.out.println("file: " + file.toString());
        System.out.println("file parent: " + file.getParentFile().toString());
        System.out.println("file_name (prefix)" + file_name);
        
        
        Collection<File> files_list_ext = FileUtils.listFiles(file.getParentFile(), new SuffixFileFilter("html"), new PrefixFileFilter(file_name)); 
        if (files_list_ext.isEmpty()) {
            throw new Exception("Could not read HTML report. ");
        }
        else {
        	html_file = files_list_ext.iterator().next();
        }

        for(String a : super.getExternalOutput()) {
        	System.out.println(a);
        	
        };

        
        System.out.println("report path: " + html_file.toString());
        
        m_data = JSViewHelper.readFile(html_file);
    	createView(inObjects, execContext);
    	
    	System.out.println("wait time: " + m_config.getWaitTime());
     
    	return outports;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        super.saveSettingsTo(settings);
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    	super.loadValidatedSettingsFrom(settings);
    	m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	super.validateSettings(settings);
    	new JSViewConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
    	
    	super.saveInternals(nodeInternDir, exec);
    	
    	// file content
    	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                new File(nodeInternDir, "loadeddata")));
        ZipEntry entry = new ZipEntry("rawdata.bin");
        out.putNextEntry(entry);
        out.write(m_data.getBytes());
        out.close();
    	
        // view representation
        NodeSettings repSettings = new NodeSettings("JSViewRepresentation");
        NodeSettings valSettings = new NodeSettings("JSViewValue");
        if (m_representation != null) {
            m_representation.saveToNodeSettings(repSettings);
        }
        if (m_viewValue != null) {
            m_viewValue.saveToNodeSettings(valSettings);
        }
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        repSettings.saveToXML(new FileOutputStream(repFile));
        valSettings.saveToXML(new FileOutputStream(valFile));
    }
    

    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    	super.loadInternals(nodeInternDir, exec);
    	
    	// HTML file content
    	ZipFile zip = new ZipFile(new File(nodeInternDir, "loadeddata"));

        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

        byte[] BUFFER = new byte[BUFFER_SIZE];

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (entry.getName().equals("rawdata.bin")) {
                int size = (int) entry.getSize();
                byte[] data = new byte[size];
                InputStream in = zip.getInputStream(entry);
                int len;
                int totlen = 0;
                while ((len = in.read(BUFFER, 0, BUFFER_SIZE)) >= 0) {
                    System.arraycopy(BUFFER, 0, data, totlen, len);
                    totlen += len;
                }
                m_data = new String(data);
            }
        }
        zip.close();
    	
    	
        File repFile = new File(nodeInternDir, "representation.xml");
        File valFile = new File(nodeInternDir, "value.xml");
        NodeSettingsRO repSettings = NodeSettings.loadFromXML(new FileInputStream(repFile));
        NodeSettingsRO valSettings = NodeSettings.loadFromXML(new FileInputStream(valFile));
        m_representation = createEmptyViewRepresentation();
        m_viewValue = createEmptyViewValue();
        try {
            m_representation.loadFromNodeSettings(repSettings);
            m_viewValue.loadFromNodeSettings(valSettings);
        } catch (InvalidSettingsException e) {
            // what to do?
            LOGGER.error("Error loading internals: " + e.getMessage(), e);
        }
    }
    
    
    /**
     * @param portIndex the port index to retrieve the table id from
     * @return a unique table id to separate interactivity events
     */
    protected final String getTableId(final int portIndex) {
        return getInHiLiteHandler(portIndex).getHiliteHandlerID().toString();
    }
    
    private IConfigurationElement getConfigurationFromWebResID(final String id) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(ID_WEB_RES);
        for (IConfigurationElement element : configurationElements) {
            if (id.equals(element.getAttribute(ATTR_RES_BUNDLE_ID))) {
                return element;
            }
        }
        return null;
    }
    
    private static final String ID_WEB_RES = "org.knime.js.core.webResources";

    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";

    private static final String ID_IMPORT_RES = "importResource";

    private static final String ATTR_PATH = "relativePath";

    private static final String ATTR_TYPE = "type";

    private void setPathsFromLibNames(final String[] libNames) {
        ArrayList<String> jsPaths = new ArrayList<String>();
        ArrayList<String> cssPaths = new ArrayList<String>();
        for (String lib : libNames) {
            IConfigurationElement confElement = getConfigurationFromWebResID(lib);
            if (confElement != null) {
                for (IConfigurationElement resElement : confElement.getChildren(ID_IMPORT_RES)) {
                    String path = resElement.getAttribute(ATTR_PATH);
                    String type = resElement.getAttribute(ATTR_TYPE);
                    if (path != null && type != null) {
                        if (type.equalsIgnoreCase("javascript")) {
                            jsPaths.add(path);
                        } else if (type.equalsIgnoreCase("css")) {
                            cssPaths.add(path);
                        }
                    } else {
                        setWarningMessage("Required library " + lib + " is not correctly configured");
                    }
                }
            } else {
                setWarningMessage("Required library is not registered: " + lib);
            }
        }
        JSViewRepresentation representation = getViewRepresentation();
        representation.setJsDependencies(jsPaths.toArray(new String[0]));
        representation.setCssDependencies(cssPaths.toArray(new String[0]));
    }

    /**
     * @param optionalViewTimeout the optionalViewTimeout to set
     */
    protected final void setOptionalViewWaitTime(final Long optionalViewTimeout) {
        m_optionalViewWaitTime = optionalViewTimeout;
    }

    /**
     * {@inheritDoc}
     */
    protected void createView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
    	synchronized (m_lock) {
    		
            JSViewRepresentation representation = getViewRepresentation();
            
           // create table with one column
        
            
            // remove style text
            //String m_html_data  = text.replaceAll("<style(.+?)</style>", "");
            
            BufferedDataContainer container = exec.createDataContainer(new DataTableSpec(
        					new DataColumnSpec[] { new DataColumnSpecCreator(
        							"Col0", StringCell.TYPE).createSpec() }));
  
            // add row
            StringCell cell = new StringCell(m_data);
            
            // add row with Html string
            container.addRowToTable(new DefaultRow(new RowKey("Row0"), new StringCell[] { cell }));
            container.close();
          
            //create JSON table if data available
            if (representation.getTable() == null) {
              //construct dataset
            	
                //BufferedDataTable table = (BufferedDataTable)inObjects[0];
                BufferedDataTable table = container.getTable();
                if (m_config.getMaxRows() < table.size()) {
                    setWarningMessage("Only the first " + m_config.getMaxRows() + " rows are displayed.");
                }
                JSONDataTable jsonTable = JSONDataTable.newBuilder()
                        .setDataTable(table)
                        .setId(getTableId(0))
                        .setFirstRow(1)
                        .setMaxRows(m_config.getMaxRows())
                        .build(exec);
                representation.setTable(jsonTable);
                JSONDataTableRow[] row = jsonTable.getRows();
                for(int i=0; i< row.length; i++ ) {  
                	Object [] obj = row[i].getData();
                	for (int j=0; j < obj.length; j++) {
                	//	System.out.println("row " + i + " col " + j + ":" + obj[j].toString());
                	}
                	
                }
                
            }
            
            //representation.setJsCode(parseTextAndReplaceVariables());
            representation.setJsCode(m_config.getJsCode());
            //representation.setJsSVGCode(m_config.getJsSVGCode());
            representation.setCssCode(m_config.getCssCode());
            setPathsFromLibNames(m_config.getDependencies());
           // setOptionalViewWaitTime((long)m_config.getWaitTime());
            setOptionalViewWaitTime((long)10);
         
          //  System.out.println("Optional View Wait time: " + (long)m_config.getWaitTime());
           // setFlowVariablesInView();
            
           // System.out.println("js code: " + representation.getJsCode());
           // System.out.println("css code: " + m_config.getCssCode());
            
            JSONDataTable theTable = representation.getTable();
            JSONDataTableRow[] row = theTable.getRows();
            System.out.println("loaded table:");
            for(int i=0; i< row.length; i++ ) {  
            	Object [] obj = row[i].getData();
            	for (int j=0; j < obj.length; j++) {
            	//	System.out.println("row " + i + " col " + j + ":" + obj[j].toString());
            	}
            	
            }
        }
    }
    
    /**
     * Resets the view HTML, attempts to delete file.
     */
    void resetViewHTML() {
        if (m_viewPath != null) {
            try {
                File viewFile = new File(m_viewPath);
                if (viewFile.exists()) {
                    viewFile.delete();
                }
            } catch (Exception e) { /* do nothing */ }
        }
        m_viewPath = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
       super.reset();
       synchronized (m_lock) {
           m_representation = createEmptyViewRepresentation();
           m_viewValue = createEmptyViewValue();
           resetViewHTML();
           m_data = "";
       }
    }
    

	@Override
	public ValidationError validateViewValue(JSViewValue viewContent) {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final JSViewValue viewValue, final boolean useAsDefault) {
        synchronized (m_lock) {
            m_viewValue = viewValue;
            if (useAsDefault) {
             //   copyValueToConfig();
            }
        }
    }

	@Override
	public void saveCurrentValue(NodeSettingsWO content) {
		// TODO Auto-generated method stub
		
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public JSViewRepresentation getViewRepresentation() {
        synchronized (m_lock) {
            return m_representation;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSViewValue getViewValue() {
        synchronized (m_lock) {
            return m_viewValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSViewRepresentation createEmptyViewRepresentation() {
        return new JSViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSViewValue createEmptyViewValue() {
        return new JSViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "knime_generic_view";
    }
	
    private String createViewPath() {
        JavaScriptViewCreator<JSViewRepresentation, JSViewValue> viewCreator =
            new JavaScriptViewCreator<JSViewRepresentation, JSViewValue>(
                getJavascriptObjectID());
        try {
            return viewCreator.createWebResources("View", getViewRepresentation(), getViewValue());
        } catch (IOException e) {
            return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewHTMLPath() {
        if (m_viewPath == null || m_viewPath.isEmpty()) {
            // view is not created
            m_viewPath = createViewPath();
        } else {
            // check if file still exists, create otherwise
            File viewFile = new File(m_viewPath);
            if (!viewFile.exists()) {
                m_viewPath = createViewPath();
            }
        }
        return m_viewPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardViewCreator<JSViewRepresentation, JSViewValue> getViewCreator() {
        return m_viewCreator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }


    
}
