package de.essbar.ngs_toolbox.IO.knime.nodes.IndexReader;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * <code>NodeDialog</code> for the "IndexReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexReaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the IndexReader node.
     */
    protected IndexReaderNodeDialog() {
    	
        super();
        
        String valid_extensions = IndexReaderNodeModel.available_index_extensions;

        final SettingsModelString index_selection = IndexReaderNodeModel.createSettingsModelSelection();
        final SettingsModelString file_selection = IndexReaderNodeModel.createSettingsModelFileSelection();
       
        final DialogComponentFileChooser file_chooser =  new DialogComponentFileChooser(file_selection, 
                "IndexReaderNodeDialog", valid_extensions);
       
        addDialogComponent(file_chooser); 
        
        addDialogComponent(new DialogComponentStringSelection(index_selection, "Index type", IndexReaderNodeModel.available_index_types));
       
        
        // add listeners to the Settings model
        // index will be determined by the selected file type
        file_chooser.addChangeListener(new ChangeListener() {
           public void stateChanged(final ChangeEvent e) {
               String index_type = IndexTypeHelper.getIndextype(file_selection.getStringValue());
               if (index_type != null) {
                   file_chooser.setToolTipText(index_type + " index");
               }
               else {
                   file_chooser.setToolTipText("unknown index type");
               }
               index_selection.setStringValue(index_type);
               index_selection.setEnabled(false);  
      
           }
 
         });
        

    	
    }
    
    

    
    
}

