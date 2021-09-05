package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.AbstractEdge;
import com.svnlib.gitcouplingtool.graph.AbstractGraph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JSONExporter<V, E extends AbstractEdge<V>> {

    private final Writer writer;

    public JSONExporter(final Writer writer) {
        this.writer = writer;
    }

    public void export(final AbstractGraph<V, E> graph) throws IOException {
        this.writer.write("{\"nodes\":[");
        this.writer.write(exportNodes(filterNodes(graph.getNodes())));
        this.writer.write("],\"edges\":[");
        this.writer.write(exportEdges(filterEdges(graph.getEdges())));
        this.writer.write("]}");
    }

    private String exportNodes(final Collection<V> nodes) {
        final List<String> formattedNodes = nodes.stream()
                                                 .map(node -> mapToJsonObject(nodeAttributes(node)))
                                                 .collect(Collectors.toList());

        return String.join(",", formattedNodes);
    }

    private String exportEdges(final Collection<E> edges) {
        final List<String> formattedEdges = edges.stream()
                                                 .map(edge -> mapToJsonObject(edgeAttributes(edge)))
                                                 .collect(Collectors.toList());

        return String.join(",", formattedEdges);
    }

    private String mapToJsonObject(final Map<String, Object> attributes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (final String key : attributes.keySet()) {
            sb.append('"').append(key).append("\":").append(objectToString(attributes.get(key))).append(',');
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}");
        return sb.toString();
    }

    private String objectToString(final Object o) {
        if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        }
        return '"' + o.toString() + '"';
    }

    protected abstract Collection<V> filterNodes(Collection<V> nodes);
    protected abstract Collection<E> filterEdges(Collection<E> edges);
    protected abstract Map<String, Object> nodeAttributes(V node);
    protected abstract Map<String, Object> edgeAttributes(AbstractEdge<V> edge);

}
