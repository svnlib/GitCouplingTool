package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.graph.UndirectedEdge;
import com.svnlib.gitcouplingtool.graph.UndirectedGraph;
import com.svnlib.gitcouplingtool.model.Artifact;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

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
                edge.setWeight(edge.getWeight() + 1);
            }
        }
    }

    @Override
    public void exportGraph(final Writer writer) throws IOException {
        writer.write(this.graph.toString());
    }

}
