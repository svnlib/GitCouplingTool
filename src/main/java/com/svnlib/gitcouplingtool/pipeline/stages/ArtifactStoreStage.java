package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.model.Artifact;
import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.store.ArtifactStore;
import teetime.stage.basic.AbstractTransformation;

import java.util.Collection;
import java.util.Set;

public class ArtifactStoreStage extends AbstractTransformation<Commit, Collection<Artifact>> {

    @Override
    protected void execute(final Commit commit) {
        final Set<Artifact> artifacts = ArtifactStore.INSTANCE.parseCommit(commit);
        this.outputPort.send(artifacts);
    }

}
