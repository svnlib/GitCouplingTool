package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.AbstractAlgorithm;
import com.svnlib.gitcouplingtool.model.Artifact;
import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.store.ArtifactStore;
import teetime.stage.basic.AbstractFilter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Set;

public class CommitAnalysisStage extends AbstractFilter<Commit> {

    private final AbstractAlgorithm algorithm;

    public CommitAnalysisStage() {
        this.algorithm = Config.algorithm.getAlgorithm();
    }

    @Override
    protected void execute(final Commit commit) {
        final Set<Artifact> artifacts = ArtifactStore.INSTANCE.parseCommit(commit);
        this.algorithm.addChangedArtifacts(new LinkedList<>(artifacts));
        this.outputPort.send(commit);
    }

    @Override
    protected void onTerminating() {
        try {
            final Writer bufferedWriter =
                    new BufferedWriter(new FileWriter(Config.output, StandardCharsets.UTF_8, false));
            this.algorithm.exportGraph(bufferedWriter);
            bufferedWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        super.onTerminating();
    }

}
