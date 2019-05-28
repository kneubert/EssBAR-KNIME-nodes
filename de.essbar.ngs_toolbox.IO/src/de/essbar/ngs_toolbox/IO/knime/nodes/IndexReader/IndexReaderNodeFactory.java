package de.essbar.ngs_toolbox.IO.knime.nodes.IndexReader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndexReader" Node.
 * 
 *
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexReaderNodeFactory 
        extends NodeFactory<IndexReaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexReaderNodeModel createNodeModel() {
        return new IndexReaderNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<IndexReaderNodeModel> createNodeView(final int viewIndex,
            final IndexReaderNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new IndexReaderNodeDialog();
    }

}

