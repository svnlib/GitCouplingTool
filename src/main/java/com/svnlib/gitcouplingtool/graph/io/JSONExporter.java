package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.AbstractEdge;
import com.svnlib.gitcouplingtool.graph.AbstractGraph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JSONExporter<E extends AbstractEdge> extends AbstractExporter<E> {

    public JSONExporter(final Writer writer) {
        super(writer);
    }

    @Override
    public void export(final AbstractGraph<E> graph) throws IOException {
        write("{\"nodes\":[");
        write(exportNodes(graph.getNodes()));
        write("],\"edges\":[");
        write(exportEdges(graph.getEdges()));
        write("]}");
    }

    @Override
    protected String exportNodes(final Collection<String> nodes) {
        final List<String> formattedNodes = nodes.stream()
                                                 .map(node -> attributesToString(nodeAttributes(node)))
                                                 .collect(Collectors.toList());

        return String.join(",", formattedNodes);
    }

    @Override
    protected String exportEdges(final Collection<E> edges) {
        final List<String> formattedEdges = edges.stream()
                                                 .map(edge -> attributesToString(edgeAttributes(edge)))
                                                 .collect(Collectors.toList());

        return String.join(",", formattedEdges);
    }

    @Override
    protected String attributesToString(final Map<String, Object> attributes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (final String key : attributes.keySet()) {
            sb.append('"').append(key).append("\":").append(enquoteString(attributes.get(key))).append(',');
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}");
        return sb.toString();
    }

    @Override
    protected Map<String, Object> nodeAttributes(final String node) {
        return Map.of("id", node);
    }

}
