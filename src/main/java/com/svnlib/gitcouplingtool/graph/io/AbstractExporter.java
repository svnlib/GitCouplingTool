package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.AbstractEdge;
import com.svnlib.gitcouplingtool.graph.AbstractGraph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractExporter<E extends AbstractEdge> {

    private final Writer writer;

    public AbstractExporter(final Writer writer) {
        this.writer = writer;
    }

    public abstract void export(final AbstractGraph<E> graph) throws IOException;

    protected abstract String exportNodes(final Collection<String> nodes);
    protected abstract String exportEdges(final Collection<E> edges);
    protected abstract String attributesToString(final Map<String, Object> attributes);
    protected abstract Map<String, Object> nodeAttributes(String node);
    protected abstract Map<String, Object> edgeAttributes(E edge);

    protected void write(final String s) throws IOException {
        this.writer.write(s);
    }

    protected String enquoteString(final Object o) {
        if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        }
        return '"' + o.toString() + '"';
    }

}
