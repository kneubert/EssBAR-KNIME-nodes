// THIS CODE WAS GENERATED WITH THE GENERIC WORKFLOW NODES FOR KNIME NODE GENERATOR
// DO NOT MODIFY
package de.essbar.ngs_toolbox.apps.knime.nodes.SPAdes;

import com.genericworkflownodes.knime.generic_node.ExecutionFailedException;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeModel;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.util.Helper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.filefilter.TrueFileFilter;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;

//import com.genericworkflownodes.knime.GenericNodesPlugin;
//import com.genericworkflownodes.knime.base.data.port.FileStorePrefixURIPortObject;
//import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
//import com.genericworkflownodes.knime.base.data.port.IPrefixURIPortObject;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutorFactory;
import com.genericworkflownodes.knime.execution.UnknownCommandGeneratorException;
import com.genericworkflownodes.knime.execution.UnknownToolExecutorException;
import com.genericworkflownodes.knime.execution.impl.CancelMonitorThread;

import com.genericworkflownodes.knime.base.data.port.*;

/**
    @brief SPAdes Node Model.
*/
public class SPAdesNodeModel extends GenericKnimeNodeModel {  
	
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(SPAdesNodeModel.class);
    
    /**
     * stores the node configuration (i.e. parameters, ports, ..)
     */
    private final INodeConfiguration m_nodeConfig;
    
    /**
     * The actual m_executor used to run the tool.
     */
    IToolExecutor m_executor;
    
    /**
     * The configuration of the encapsulating plugin.
     */
    private final IPluginConfiguration m_pluginConfig;
    
    protected SPAdesNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig) {
        super(nodeConfig, pluginConfig, new String[][]{{"fastq" ,"fq"}, {"fastq" ,"fq" }, {"fastq" ,"fq" }, {"fastq" ,"fq", "bam" }, {"fastq" ,"fq" }, {"fastq" ,"fq" }, {"fastq" ,"fq" },}, new String[][]{{"fasta"}});
        m_nodeConfig = nodeConfig;
        m_pluginConfig = pluginConfig;
    }
    
    
    @Override
    protected PortObject[] execute(PortObject[] inObjects,
            ExecutionContext execContext) throws Exception {
        // create job directory
        // java.lang.NoClassDefFoundError: com/genericworkflownodes/knime/GenericNodesPlugin
    	//File jobdir = Helper.getTempDir(m_nodeConfig.getName(),
        //        !GenericNodesPlugin.isDebug());

    	// quick workaround
    	File jobdir = Helper.getTempDir(m_nodeConfig.getName(),false);

        // transfer the incoming files into the nodeConfiguration
        System.out.println("transfer incoming ports2config");
        transferIncomingPorts2Config(inObjects);

        // prepare input data and parameter values
        System.out.println("prepare input data and parameter values");
        List<PortObject> outPorts = transferOutgoingPorts2Config(jobdir,
                inObjects, execContext);

        // prepare the executor
        System.out.println("prepare executor");
        m_executor = prepareExecutor(jobdir);

        // launch executable
        System.out.println("launch executable");
        executeTool(m_executor, execContext);

        // process result files
        // PortObject[] outports = processOutput(outputFiles, exec);

        /*
        if (!GenericNodesPlugin.isDebug()) {
            FileUtils.deleteDirectory(jobdir);
        }
        */

        PortObject[] outports = new PortObject[outPorts.size()];
        for (int i = 0; i < outPorts.size(); ++i) {
            outports[i] = outPorts.get(i);
            // if we have an prefix port we need to trigger reindexing
            if (outports[i] instanceof FileStorePrefixURIPortObject) {
                ((FileStorePrefixURIPortObject) outports[i]).collectFiles();
            }
        }

        return outports;
    }
    

    

    /**
     * Instantiates a new {@link IToolExecutor} for this tool according to the
     * plug-in settings.
     * 
     * @param jobdir
     *            The directory assigned to the node by the
     *            {@link ExecutionContext}.
     * @throws UnknownToolExecutorException
     *             Thrown if the requested {@link IToolExecutor} is unknown.
     * @throws UnknownCommandGeneratorException
     *             Thrown if the requested {@link ICommandGenerator} is unknown.
     * @throws IOException
     * @throws Exception
     */
    private IToolExecutor prepareExecutor(File jobdir)
            throws UnknownToolExecutorException,
            UnknownCommandGeneratorException, IOException, Exception {
        IToolExecutor executor = ToolExecutorFactory.createToolExecutor(
                m_pluginConfig.getPluginProperties().getProperty("executor"),
                m_pluginConfig.getPluginProperties().getProperty(
                        "commandGenerator"));

        executor.setWorkingDirectory(jobdir);
        executor.prepareExecution(m_nodeConfig, m_pluginConfig);

        return executor;
    }
    
    
    /**
     * Executes the tool underlying this node.
     * 
     * @param executor
     *            The fully configured {@link IToolExecutor}.
     * 
     * @param execContext
     *            The {@link ExecutionContext} of the node.
     * 
     * @throws Exception
     */
    private void executeTool(IToolExecutor executor,
            final ExecutionContext execContext) throws ExecutionFailedException {

        final AsynchronousToolExecutor asyncExecutor = new AsynchronousToolExecutor(
                executor);

        asyncExecutor.invoke();

        // create one thread that will periodically check if the user has
        // cancelled the execution of the node
        // if this monitor thread detects that a cancel was requested, then it
        // will invoke the kill method
        // of the asyncExecutor
        final CancelMonitorThread monitorThread = new CancelMonitorThread(
                asyncExecutor, execContext);
        monitorThread.start();

        // wait until the execution completes
        asyncExecutor.waitUntilFinished();
        // also wait for the monitor thread to die
        monitorThread.waitUntilFinished();
        System.out.println("m_nodeConfig.getName(): " + m_nodeConfig.getName().toString());
        
        int retcode = -1;
        try {
            retcode = asyncExecutor.getReturnCode();
        } catch (ExecutionException ex) {
            // it means that the task threw an exception, assume retcode == -1
            throw new ExecutionFailedException(m_nodeConfig.getName(), ex);
        } catch (InterruptedException iex) {
            throw new ExecutionFailedException(m_nodeConfig.getName(), iex);
        }
        
        LOGGER.debug("COMMAND:  " + executor.getCommand());
        LOGGER.debug("STDOUT:  " + executor.getToolOutput());
        LOGGER.debug("STDERR:  " + executor.getToolErrorOutput());
        LOGGER.debug("RETCODE: " + retcode);
        
        
        System.out.println("COMMAND:  " + executor.getCommand());
       // System.out.println("STDOUT:  " + executor.getToolOutput());
        System.out.println("STDERR:  " + executor.getToolErrorOutput());
        System.out.println("RETCODE: " + retcode);
        
        
        if (retcode != 0) {
            LOGGER.error("Failing process stdout: " + executor.getToolOutput());
            LOGGER.error("Failing process stderr: "
                    + executor.getToolErrorOutput());

            // process failed, so we will send the stdout/stderr messages into
            // the dialogs
            setFailedExternalOutput(executor.getToolOutput());
            setFailedExternalErrorOutput(executor.getToolErrorOutput());

            throw new ExecutionFailedException(m_nodeConfig.getName());
        }

        // finally fill the stdout/stderr messages into the dialogs
        setExternalOutput(executor.getToolOutput());
        setExternalErrorOutput(executor.getToolErrorOutput());

    }

    
    /**
     * Transfers the incoming ports into the config, that it can be written out
     * into a config file or can be transferred to the command line.
     * 
     * @param inData
     *            The incoming port objects.
     * @throws Exception
     */
    private void transferIncomingPorts2Config(PortObject[] inData)
            throws Exception {
        // Transfer settings from the input ports into the configuration object
        for (int i = 0; i < inData.length; i++) {
            // find the internal port for this PortObject
            Port port = m_nodeConfig.getInputPorts().get(i);

            IURIPortObject po = (IURIPortObject) inData[i];
            

            String name = port.getName();
            // find the associated parameter in the configuration
            Parameter<?> p = m_nodeConfig.getParameter(name);
            
            boolean isMultiFile = port.isMultiFile();
            boolean isPrefix = port.isPrefix();
            
            // skip optional and unconnected inport ports
            if (inData[i] == null) {
                p.setValue(null);
                continue;
            }
            
            // connected: check contents
            List<URIContent> uris = po.getURIContents();

            // check validity of subtypes with actual inputs
            if (uris.size() > 1 && (!isMultiFile && !isPrefix)) {
                throw new Exception(
                        "IURIPortObject with multiple URIs supplied at single URI port #"
                                + i);
            }

            // check that we are actually referencing a file parameter from this
            // port
            if (!(p instanceof IFileParameter)) {
                throw new Exception(
                        "Invalid reference from port to non-file parameter. URI port #"
                                + i);
            }

            if (isPrefix) {
                // we pass only the prefix to the tool
                IPrefixURIPortObject puri = (IPrefixURIPortObject) inData[i];
                ((FileParameter) p).setValue(puri.getPrefix());
            } else if (isMultiFile) {
                // we need to collect all filenames and then set them as a batch
                // in the config
                List<String> filenames = new ArrayList<String>();
                for (URIContent uric : uris) {
                    URI uri = uric.getURI();
                    filenames.add(new File(uri).getAbsolutePath());
                }
                ((FileListParameter) p).setValue(filenames);
            } else {
                // just one filename
                URI uri = uris.get(0).getURI();
                String filename = new File(uri).getAbsolutePath();
                ((FileParameter) p).setValue(filename);
            }
        }
    }

    /**
     * Creates a list of lists of output files (as {@link URI}s) pointing to the
     * files that will be generated by the executed tool.
     * 
     * @param jobdir
     *            The working directory of the executable.
     * @param inData
     *            The input data as {@link PortObject} array
     * @return A list of lists of output files
     * @throws Exception
     *             If the input has an invalid configuration.
     */
    private List<PortObject> transferOutgoingPorts2Config(final File jobdir,
            PortObject[] inData, ExecutionContext exec) throws Exception {

        final int nOut = m_nodeConfig.getOutputPorts().size();
        List<PortObject> outPorts = new ArrayList<PortObject>(nOut);

        System.out.println("process " + nOut + "output ports");
        for (int i = 0; i < nOut; i++) {
            Port port = m_nodeConfig.getOutputPorts().get(i);
            System.out.println("process port " + port.getName());
            //Before we know about its extension, a port is reset to active
            System.out.println("Set port active");
            port.setActive(true);
            System.out.println("Get name of port");
            String name = port.getName();
            String ext = "";
          
            boolean isPrefix = port.isPrefix();
            System.out.println("is prefix " + isPrefix);
            if (!isPrefix){
                ext = getOutputType(i);
            }
            if (ext.toLowerCase().equals("inactive")) {
                port.setActive(false);
                outPorts.add(InactiveBranchPortObject.INSTANCE);
                continue;
            }
            System.out.println("get parameter with " + name);
            Parameter<?> p = m_nodeConfig.getParameter(name);

            // basenames and number of output files guessed from input
            System.out.println("getOutputBaseNames");
            List<String> basenames = getOutputBaseNames();
            System.out.println("check if port is multifile");
            boolean multif = port.isMultiFile();
            System.out.println("check passed");
            if (p instanceof FileListParameter && port.isMultiFile()) {
                // we currently do not support lists of prefixes
            	
                if (isPrefix) {
                    throw new InvalidSettingsException(
                            "GKN currently does not support lists of prefixes as output.");
                }
                System.out.println("check passed");
                FileListParameter flp = (FileListParameter) p;
                List<String> fileNames = new ArrayList<String>();

                if (basenames.size() == 0) {
                    throw new Exception(
                            "Cannot determine number of output files if no input file is given.");
                }

                System.out.println("createFileStore for " + m_nodeConfig.getName());
                PortObject fsupo = new FileStoreURIPortObject(
                        exec.createFileStore(m_nodeConfig.getName() + "_" + i));

                for (int f = 0; f < basenames.size(); ++f) {
                    // create basename: <base_name>_<port_nr>_<outfile_nr>
                    String file_basename = String.format("%s_%d",
                            basenames.get(f), f);
                    File file = ((FileStoreURIPortObject) fsupo).registerFile(file_basename + "." + ext);
                    fileNames.add(file.getAbsolutePath());
                }
                
                // add filled portobject
                outPorts.add(fsupo);

                // overwrite existing settings with new values generated by the
                // stash
                flp.setValue(fileNames);

            } else if (p instanceof FileParameter && !port.isMultiFile()) {
            	System.out.println("port is a FileParameter and no multiFile");
                PortObject po;
                // if we have no basename to use (e.g., Node without input-file)
                // we use the nodename
                String basename;
                if (basenames.isEmpty()) { // modify that, it should check if basenames.get(0) is null
                //if (basenames.isEmpty() || basenames.get(0) == null) {
                	System.out.println("basenames is empty or null");
                    basename = m_nodeConfig.getName();
                } else {
                   	System.out.println("basenames is not empty");
                    basename = basenames.get(0);
                    System.out.println("basename is " + basename);
                }

                // create basename: <base_name>_<outfile_nr>
               // ext = "fasta";
                
                String fileName = basename;
    
                if (!isPrefix){
                    fileName += '.' + ext;
                }
                // overwrite
                fileName = "scaffolds.fasta";
                
                if (isPrefix) {
                	System.out.println("create FileStorePrefixURIPortObject");
                	System.out.println("name of the tool " + m_nodeConfig.getName());
                	System.out.println("filename: " + fileName);
                    po = new FileStorePrefixURIPortObject(
                            exec.createFileStore( m_nodeConfig.getName() + "_"
                                    + i), fileName);
                    System.out.println("prefix: " + ((FileStorePrefixURIPortObject)po).getPrefix());
                    System.out.println("set Value:" + ((FileStorePrefixURIPortObject) po).getPrefix());
                    ((FileParameter) p).setValue(((FileStorePrefixURIPortObject) po).getPrefix());
                    LOGGER.debug("> setting param " + name + "->"
                            + ((FileStorePrefixURIPortObject) po).getPrefix());
                    System.out.println("> setting param " + name + "->"
                            + ((FileStorePrefixURIPortObject) po).getPrefix());

                } else {
                    po = new FileStoreURIPortObject(
                            exec.createFileStore( m_nodeConfig.getName() + "_"
                                    + i));
                   
                    // we do not append the file extension if we have a prefix
                    File file = ((FileStoreURIPortObject)po).registerFile(fileName);
                    ((FileParameter) p).setValue(file.getAbsolutePath());
                    LOGGER.debug("> setting param " + name + "->" + file);
                }
                // remember output file 
                outPorts.add(po);
            } else {
                throw new Exception(
                        "Invalid connection between ports and parameters.");
            }
        }
        return outPorts;
    }

    
    /**
     * Tries to guess the optimal output file names given all the input edges.
     * The file names will be extracted from the configuration, hence the file
     * names need to be transferred into config prior to using this method. See
     * {@link GenericKnimeNodeModel#transferIncomingPorts2Config(PortObject[])}.
     * 
     * @return A list of base names for the output files.
     * @throws Exception
     */
    private List<String> getOutputBaseNames() throws Exception {

        // 1. we select always the list with the highest number of files.
        // 2. we prefer lists over files (independent of the number of
        // elements).
        // 3. we prefer files over prefixes since we assume that prefixes are
        // often indices or reference data
        // 4. ignore optional parameters

        List<String> basenames = new ArrayList<String>();

        // find the port
        int naming_port = 0;
        int max_size = -1;
        boolean seen_prefix = false;
        boolean isFileParameter = false;
        for (int i = 0; i < m_nodeConfig.getInputPorts().size(); ++i) {
            Port port = m_nodeConfig.getInputPorts().get(i);
            String name = port.getName();
            Parameter<?> p = m_nodeConfig.getParameter(name);

            // we don't assume that optional ports are naming relevant
            if (p.isOptional()) {
                continue;
            }

            if (p instanceof FileListParameter) {
                FileListParameter flp = (FileListParameter) p;
                if (max_size == -1
                        || (isFileParameter && (max_size <= flp.getValue()
                                .size()))) {
                    max_size = flp.getValue().size();
                    naming_port = i;
                } else if (flp.getValue().size() != max_size) {
                    throw new Exception(
                            "The number of output files cannot be determined since multiple input file lists with disagreeing numbers exist.");

                }
            } else if (max_size == -1 || seen_prefix) {
                // is a regular incoming port but we have no better option
                max_size = 1;
                naming_port = i;
                // indicating that we have (for now) selected a file parameter
                // which will be overruled by any FileListParameter
                isFileParameter = true;
                seen_prefix = port.isPrefix();
            }
        }

        if (m_nodeConfig.getInputPorts().size() > 0) {
            // generate the filenames if there are input ports
            // without ports, the names are set in transferOutgoingPorts2Config
            Port port = m_nodeConfig.getInputPorts().get(naming_port);
            String name = port.getName();
            Parameter<?> p = m_nodeConfig.getParameter(name);

            if (p instanceof FileListParameter) {
                // we have multiple base names
                FileListParameter flp = (FileListParameter) p;
                for (String fName : flp.getValue()) {
                    basenames.add(FilenameUtils.getBaseName(fName));
                }
            } else {
                // we only have a single basename
                // FilenameUtils.getBaseName()
                basenames.add(FilenameUtils.getBaseName(((FileParameter) p)
                        .getValue()));
            }
        }

        return basenames;
    }

    
}
