package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.ArtifactStore;
import org.eclipse.jgit.diff.DiffEntry;
import teetime.stage.basic.AbstractTransformation;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ArtifactStoreStage extends AbstractTransformation<List<DiffEntry>, Collection<Artifact>> {

    @Override
    protected void execute(final List<DiffEntry> commit) {
        final Set<Artifact> artifacts = ArtifactStore.INSTANCE.applyDiffsOnArtifacts(commit);
        this.outputPort.send(artifacts);
    }

}
