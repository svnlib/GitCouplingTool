package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.Edge;
import com.svnlib.gitcouplingtool.graph.Graph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/**
 * Superclass for an exporter exporting graphs with a given type of edge.
 *
 * @param <E> the type of the edge
 */
public abstract class AbstractExporter {

    private final Writer writer;

    /**
     * @param writer the {@link Writer} to write the result to
     */
    public AbstractExporter(final Writer writer) {
        this.writer = writer;
    }

    /**
     * Export a given graph to the {@link Writer}
     *
     * @param graph the graph to export
     */
    public abstract void export(final Graph graph) throws IOException;

    protected abstract String exportNodes(final Collection<String> nodes);
    protected abstract String exportEdges(final Collection<Edge> edges);
    protected abstract String attributesToString(final Map<String, Object> attributes);
    protected abstract Map<String, Object> nodeAttributes(String node);
    protected abstract Map<String, Object> edgeAttributes(Edge edge);

    /**
     * Write a {@link String} to the writer.
     *
     * @param string the {@link String} to write
     */
    protected void write(final String string) throws IOException {
        this.writer.write(string);
    }

    /**
     * Converts everything stringlike to an enquote string and other types to a string
     *
     * @param o the object to be converted
     *
     * @return the result
     */
    protected String enquoteString(final Object o) {
        if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        }
        return '"' + o.toString() + '"';
    }

}
