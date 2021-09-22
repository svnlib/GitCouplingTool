package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.Edge;
import com.svnlib.gitcouplingtool.graph.Graph;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A graph exporter to generate the GML file format.
 */
public class GMLExporter extends AbstractExporter {

    public GMLExporter(final Writer writer) {
        super(writer);
    }

    @Override
    public void export(final Graph graph) throws IOException {
        this.export(graph.getNodes().iterator(), graph.getEdges().iterator());
    }

    @Override
    public void export(final Iterator<String> nodeIterator, final Iterator<Edge> edgeIterator) throws IOException {

        write("graph [\n");
        exportNodes(nodeIterator);
        exportEdges(edgeIterator);
        write("\n]");
    }

    @Override
    protected void exportNodes(final Iterator<String> nodes) throws IOException {
        while (nodes.hasNext()) {
            final String node = nodes.next();
            write("node [\n" + attributesToString(nodeAttributes(node)) + "\n]\n");
        }
    }

    @Override
    protected void exportEdges(final Iterator<Edge> edges) throws IOException {
        while (edges.hasNext()) {
            final Edge edge = edges.next();
            write("edge [\n" + attributesToString(edgeAttributes(edge)) + "\n]\n");
        }
    }

    @Override
    protected String attributesToString(final Map<String, Object> attributes) {
        return attributes.keySet()
                         .stream()
                         .map(a -> a + " " + enquoteString(attributes.get(a)))
                         .collect(Collectors.joining("\n"));
    }

    @Override
    protected Map<String, Object> nodeAttributes(final String node) {
        return Map.of("id", node, "label", node);
    }

    @Override
    protected Map<String, Object> edgeAttributes(final Edge edge) {
        return Map.of("source",
                      edge.getSrc(),
                      "target",
                      edge.getDest(),
                      "value",
                      edge.getWeight(),
                      "directed",
                      edge.isDirected() ? 1 : 0);
    }

}
