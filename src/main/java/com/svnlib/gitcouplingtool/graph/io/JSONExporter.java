package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.Edge;
import com.svnlib.gitcouplingtool.graph.Graph;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * A graph exporter to generate the JSON file format.
 */
public class JSONExporter extends AbstractExporter {

    public JSONExporter(final Writer writer) {
        super(writer);
    }

    @Override
    public void export(final Graph graph) throws IOException {
        export(graph.getNodes().iterator(), graph.getEdges().iterator());
    }

    @Override
    public void export(final Iterator<String> nodeIterator, final Iterator<Edge> edgeIterator) throws IOException {

        write("{\"nodes\":[");
        exportNodes(nodeIterator);
        write("],\"edges\":[");
        exportEdges(edgeIterator);
        write("]}");
    }

    @Override
    protected void exportNodes(final Iterator<String> nodes) throws IOException {
        while (nodes.hasNext()) {
            final String node = nodes.next();
            write(attributesToString(nodeAttributes(node)));
            if (nodes.hasNext()) {
                write(",");
            }
        }
    }

    @Override
    protected void exportEdges(final Iterator<Edge> edges) throws IOException {
        while (edges.hasNext()) {
            final Edge edge = edges.next();
            write(attributesToString(edgeAttributes(edge)));
            if (edges.hasNext()) {
                write(",");
            }
        }
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

    @Override
    protected Map<String, Object> edgeAttributes(final Edge edge) {
        return Map.of("id",
                      edge.getSrc() + "::" + edge.getDest(),
                      "start",
                      edge.getSrc(),
                      "end",
                      edge.getDest(),
                      "weight",
                      edge.getWeight(),
                      "directed",
                      edge.isDirected());
    }

}
