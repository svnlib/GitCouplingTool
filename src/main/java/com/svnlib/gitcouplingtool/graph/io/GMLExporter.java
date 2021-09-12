package com.svnlib.gitcouplingtool.graph.io;

import com.svnlib.gitcouplingtool.graph.Edge;
import com.svnlib.gitcouplingtool.graph.Graph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
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
        write("graph [\n");
        write(exportNodes(graph.getNodes()));
        write(exportEdges(graph.getEdges()));
        write("\n]");
    }

    @Override
    protected String exportNodes(final Collection<String> nodes) {
        return nodes.stream()
                    .map(node -> "node\n[\n" + attributesToString(nodeAttributes(node)) + "\n]")
                    .collect(Collectors.joining("\n"));
    }

    @Override
    protected String exportEdges(final Collection<Edge> edges) {
        return edges.stream()
                    .map(edge -> "edge\n[\n" + attributesToString(edgeAttributes(edge)) + "\n]")
                    .collect(Collectors.joining("\n"));
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
