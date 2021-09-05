package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.graph.AbstractEdge;
import com.svnlib.gitcouplingtool.graph.UndirectedEdge;
import com.svnlib.gitcouplingtool.graph.UndirectedGraph;
import com.svnlib.gitcouplingtool.graph.io.JSONExporter;
import com.svnlib.gitcouplingtool.model.Artifact;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UndirectedRawCounting extends AbstractAlgorithm {

    UndirectedGraph<Artifact> graph = new UndirectedGraph<>();

    @Override
    public void addArtifact(final Artifact artifact) {
        this.graph.addVertex(artifact);
    }

    @Override
    public void addChangedArtifacts(final List<Artifact> artifacts) {
        for (int i = 0; i < artifacts.size(); i++) {
            for (int j = i + 1; j < artifacts.size(); j++) {
                final Artifact                 src  = artifacts.get(i);
                final Artifact                 dest = artifacts.get(j);
                final UndirectedEdge<Artifact> edge = this.graph.findOrCreateEdge(src, dest, 0);
                synchronized (edge) {
                    edge.setWeight(edge.getWeight() + 1);
                }
            }
        }
    }

    @Override
    public void exportGraph(final Writer writer) throws IOException {
        final JSONExporter<Artifact, UndirectedEdge<Artifact>> exporter = new JSONExporter<>(writer) {
            @Override
            protected Collection<Artifact> filterNodes(final Collection<Artifact> nodes) {
                return nodes.stream()
                            .filter(node -> node.getChangeCount() > 1)
                            .sorted(Comparator.comparingDouble(Artifact::getChangeCount).reversed())
                            .collect(Collectors.toList());
            }

            @Override
            protected Collection<UndirectedEdge<Artifact>> filterEdges(
                    final Collection<UndirectedEdge<Artifact>> edges) {
                final AtomicInteger count = new AtomicInteger();
                return edges.stream()
                            .filter(edge -> edge.getWeight() > 1)
                            .sorted(Comparator.comparingDouble(UndirectedEdge<Artifact>::getWeight).reversed())
                            .filter(o -> count.getAndIncrement() < 100)
                            .collect(Collectors.toList());
            }

            @Override
            protected Map<String, Object> nodeAttributes(final Artifact node) {
                return Map.of("id",
                              node.getId(),
                              "label",
                              node.getOriginalPath(),
                              "value",
                              node.getChangeCount());
            }

            @Override
            protected Map<String, Object> edgeAttributes(final AbstractEdge<Artifact> edge) {
                return Map.of("id",
                              edge.getSrc().getId() + "::" + edge.getDest().getId(),
                              "start",
                              edge.getSrc().getId(),
                              "end",
                              edge.getDest().getId(),
                              "weight",
                              (int) edge.getWeight(),
                              "directed",
                              false);
            }
        };
        exporter.export(this.graph);
    }

}
