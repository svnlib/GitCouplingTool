package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.ArtifactStore;
import com.svnlib.gitcouplingtool.algorithm.CouplingAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import teetime.stage.basic.AbstractTransformation;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AlgorithmStage extends AbstractTransformation<List<DiffEntry>, Collection<Artifact>> {

    private final CouplingAlgorithm algorithm;

    public AlgorithmStage(final CouplingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void execute(final List<DiffEntry> commit) throws Exception {
        final Set<Artifact> artifacts = ArtifactStore.INSTANCE.applyDiffsOnArtifacts(commit);
        this.algorithm.changedArtifacts(artifacts);
        this.outputPort.send(artifacts);
    }

}
