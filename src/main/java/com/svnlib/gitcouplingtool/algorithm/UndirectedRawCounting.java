package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.model.Artifact;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.json.JSONExporter;

import java.io.Writer;
import java.util.List;
import java.util.Map;

public class UndirectedRawCounting extends AbstractAlgorithm {

    Graph<Artifact, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    @Override
    public void addArtifact(final Artifact artifact) {
        this.graph.addVertex(artifact);
    }

    @Override
    public void addChangedArtifacts(final List<Artifact> artifacts) {
        for (int i = 0; i < artifacts.size(); i++) {
            for (int j = i + 1; j < artifacts.size(); j++) {
                final Artifact            src  = artifacts.get(i);
                final Artifact            dest = artifacts.get(j);
                final DefaultWeightedEdge edge = this.graph.getEdge(src, dest);
                if (edge == null) {
                    this.graph.addEdge(src, dest);
                } else {
                    final double edgeWeight = this.graph.getEdgeWeight(edge);
                    this.graph.setEdgeWeight(edge, edgeWeight + 1);
                }
            }
        }
    }

    @Override
    public void exportGraph(final Writer writer) {
        final JSONExporter<Artifact, DefaultWeightedEdge> exporter =
                new JSONExporter<>(Artifact::getOriginalPath);
        exporter.setEdgeAttributeProvider(defaultWeightedEdge -> Map.of("weight",
                                                                        DefaultAttribute.createAttribute((int) this.graph.getEdgeWeight(
                                                                                defaultWeightedEdge))));
        exporter.exportGraph(this.graph, writer);
    }

}
