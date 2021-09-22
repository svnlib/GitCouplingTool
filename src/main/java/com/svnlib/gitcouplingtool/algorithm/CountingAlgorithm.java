package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.graph.Edge;
import com.svnlib.gitcouplingtool.graph.Graph;
import com.svnlib.gitcouplingtool.graph.io.AbstractExporter;
import com.svnlib.gitcouplingtool.graph.io.GMLExporter;
import com.svnlib.gitcouplingtool.graph.io.JSONExporter;
import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import com.svnlib.gitcouplingtool.util.PushList;
import me.tongfei.progressbar.ProgressBar;

import java.io.Writer;
import java.util.*;

/**
 * A super class for all algorithms based on counting file changes.
 */
public abstract class CountingAlgorithm implements CouplingAlgorithm {

    /** Fast way to find a matrix index for a given {@link Artifact}. */
    private final Map<Artifact, Integer> artifactToIdxIndex;
    /** Fast way to find an {@link Artifact} for a given matrix index. */
    private final List<Artifact>         idxToArtifactIndex;
    /** A triangular matrix with the size of given artifact count. */
    private final int[][]                countingMatrix;

    private final Graph graph;

    /**
     * Initializes the algorithm by creating the counting matrix and the indices.
     *
     * @param artifacts the artifacts to be indexed
     */
    protected CountingAlgorithm(final Collection<Artifact> artifacts) {

        this.graph = new Graph();

        final int artifactCount = artifacts.size();
        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Building Counting Matrix", "Rows")
                                               .setInitialMax(artifactCount * 2L)
                                               .build();

        this.artifactToIdxIndex = new HashMap<>();
        this.idxToArtifactIndex = new ArrayList<>();
        int idx = 0;
        for (final Artifact artifact : artifacts) {
            this.artifactToIdxIndex.put(artifact, idx++);
            this.idxToArtifactIndex.add(artifact);
            pb.step();
        }

        this.countingMatrix = new int[artifactCount][];
        for (int i = 0; i < artifactCount; i++) {
            this.countingMatrix[i] = new int[i + 1];
            pb.step();
        }

        pb.close();
    }

    /**
     * Creates an {@link Edge} for given artifacts and change count.
     *
     * @param a           the first {@link Artifact}
     * @param b           the second {@link Artifact}
     * @param aCount      how often artifact a changed at all
     * @param bCount      how often artifact b changed at all
     * @param commonCount how often both artifacts changed at the same time
     *
     * @return the created edge
     */
    protected abstract Collection<Edge> createEdges(Artifact a, Artifact b, int aCount, int bCount, int commonCount);

    @Override
    public void changedArtifacts(final Collection<Artifact> artifacts) {
        for (final Artifact artifact1 : artifacts) {
            for (final Artifact artifact2 : artifacts) {
                final int idx1 = this.artifactToIdxIndex.get(artifact1);
                final int idx2 = this.artifactToIdxIndex.get(artifact2);

                if (idx1 >= idx2) {
                    synchronized (this.countingMatrix[idx1]) {
                        this.countingMatrix[idx1][idx2]++;
                    }
                }
            }
        }
    }

    @Override
    public void execute() {
        if (Config.edgeCount < 1) {
            return;
        }
        final PushList<Edge> edges = new PushList<>(Config.edgeCount);
        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Building Graph", "Steps")
                                               .setInitialMax((((long) this.countingMatrix.length - 1) *
                                                               (this.countingMatrix.length - 1)) / 2 + Config.edgeCount)
                                               .build();

        for (int i = 0; i < this.countingMatrix.length; i++) {
            for (int j = 0; j < i; j++) {
                pb.step();
                final int commonCount = this.countingMatrix[i][j];
                if (commonCount < Config.minCouplings) {
                    continue;
                }
                createEdges(this.idxToArtifactIndex.get(i),
                            this.idxToArtifactIndex.get(j),
                            this.countingMatrix[i][i],
                            this.countingMatrix[j][j],
                            commonCount)
                        .forEach(edges::add);
            }
        }

        edges.toList().forEach(e -> {
            this.graph.addEdge(e);
            this.graph.addNode(e.getSrc());
            this.graph.addNode(e.getDest());
            pb.step();
        });

        pb.close();
    }

    @Override
    public void export(final Writer writer) throws Exception {
        final AbstractExporter exporter;
        switch (Config.format) {
            case JSON:
                exporter = new JSONExporter(writer);
                break;
            case GML:
                exporter = new GMLExporter(writer);
                break;
            default:
                return;
        }
        if (Config.edgeCount > 0) {
            exporter.export(this.graph);
        } else {
            exporter.export(new NodeIterator(), new EdgeIterator());
        }
    }

    private class EdgeIterator implements Iterator<Edge> {

        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Export Edges", "Edges")
                                               .setInitialMax(
                                                       (((long) CountingAlgorithm.this.countingMatrix.length - 1) *
                                                        (CountingAlgorithm.this.countingMatrix.length - 1)) / 2)
                                               .build();

        private int row = 1;
        private int col = 0;

        Deque<Edge> previousEdges = new LinkedList<>();

        @Override
        public boolean hasNext() {
            if (!this.previousEdges.isEmpty()) {
                return true;
            } else {
                skip();
                return this.row < CountingAlgorithm.this.countingMatrix.length;
            }
        }

        @Override
        public Edge next() {
            if (!this.previousEdges.isEmpty()) {
                return this.previousEdges.poll();
            }
            skip();
            final int commonCount = CountingAlgorithm.this.countingMatrix[this.row][this.col];
            this.previousEdges.addAll(createEdges(CountingAlgorithm.this.idxToArtifactIndex.get(this.row),
                                                  CountingAlgorithm.this.idxToArtifactIndex.get(this.col),
                                                  CountingAlgorithm.this.countingMatrix[this.row][this.row],
                                                  CountingAlgorithm.this.countingMatrix[this.col][this.col],
                                                  commonCount));

            movePointer();
            return this.previousEdges.poll();
        }

        private void skip() {
            while (this.row < CountingAlgorithm.this.countingMatrix.length &&
                   CountingAlgorithm.this.countingMatrix[this.row][this.col] < Config.minCouplings) {
                movePointer();
            }
        }

        private void movePointer() {
            this.pb.step();
            this.col = (this.col + 1) % this.row;
            if (this.col == 0) {
                this.row++;
            }
        }

    }

    private class NodeIterator implements Iterator<String> {

        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Exporting Nodes", "Nodes")
                                               .setInitialMax(CountingAlgorithm.this.countingMatrix.length)
                                               .build();

        private int idx = 0;

        @Override
        public boolean hasNext() {
            skip();
            return this.idx < CountingAlgorithm.this.countingMatrix.length;
        }

        @Override
        public String next() {
            this.pb.step();
            return CountingAlgorithm.this.idxToArtifactIndex.get(this.idx++).getOriginalPath();
        }

        private void skip() {
            while (this.idx < CountingAlgorithm.this.countingMatrix.length) {
                for (int i = 0; i < CountingAlgorithm.this.countingMatrix.length; i++) {
                    if (i == this.idx) {
                        continue;
                    }

                    if (i < this.idx && CountingAlgorithm.this.countingMatrix[this.idx][i] >= Config.minCouplings) {
                        return;
                    }
                    if (i > this.idx && CountingAlgorithm.this.countingMatrix[i][this.idx] >= Config.minCouplings) {
                        return;
                    }
                }
                this.pb.step();
                this.idx++;
            }
        }

    }

}
