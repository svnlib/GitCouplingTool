package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import me.tongfei.progressbar.ProgressBar;

import java.util.*;

public abstract class CountingAlgorithm implements CouplingAlgorithm {

    private final Map<Artifact, Integer> artifactToIdxIndex;
    private final List<Artifact>         idxToArtifactIndex;
    private final int[][]                countingMatrix;

    protected CountingAlgorithm(final Collection<Artifact> artifacts) {
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

    @Override
    public void execute() {
        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Creating edges", "Edges")
                                               .setInitialMax(((long) this.countingMatrix.length - 1) *
                                                              (this.countingMatrix.length - 1) / 2).build();

        for (int i = 0; i < this.countingMatrix.length; i++) {
            for (int j = 0; j < i; j++) {
                final int commonCount = this.countingMatrix[i][j];
                if (commonCount <= 1) {
                    continue;
                }
                addToGraph(this.idxToArtifactIndex.get(i),
                           this.idxToArtifactIndex.get(j),
                           this.countingMatrix[i][i],
                           this.countingMatrix[j][j],
                           commonCount);
                pb.step();
            }
        }

        pb.close();
    }

    protected abstract void addToGraph(Artifact a, Artifact b, int aCount, int bCount, int commonCount);

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

    protected Set<Artifact> getArtifacts() {
        return this.artifactToIdxIndex.keySet();
    }

    protected int getChangeCount(final Artifact artifact) {
        final int idx = this.artifactToIdxIndex.get(artifact);
        return this.countingMatrix[idx][idx];
    }

    protected int getCommonChangeCount(final Artifact a, final Artifact b) {
        final int idx1 = this.artifactToIdxIndex.get(a);
        final int idx2 = this.artifactToIdxIndex.get(b);
        if (idx1 > idx2) {
            return this.countingMatrix[idx1][idx2];
        } else {
            return -1;
        }
    }

}
