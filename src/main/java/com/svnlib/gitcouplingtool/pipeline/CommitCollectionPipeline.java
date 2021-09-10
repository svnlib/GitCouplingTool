package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.pipeline.stages.*;
import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CommitCollectionPipeline extends AbstractPipeline {

    private final CollectorSink<List<DiffEntry>> collectorStage;

    public CommitCollectionPipeline() throws IOException {
        final CommitProducerStage     commitProducerStage     = new CommitProducerStage();
        final CommitMetaFilter        commitMetaFilter        = new CommitMetaFilter();
        final CombineConsecutiveStage combineConsecutiveStage = new CombineConsecutiveStage();

        final Distributor<List<RevCommit>> revCommitDistributor = new Distributor<>();
        final Merger<List<DiffEntry>>      revCommitMerger      = new Merger<>();

        final CommitDiffCountFilter commitDiffCountFilter = new CommitDiffCountFilter();

        final ProgressBarStage<List<DiffEntry>> progressBarStage = new ProgressBarStage<>();
        ProgressBarUtils.addToBuilder(progressBarStage.builder(), "Collecting", "Commits");

        this.collectorStage = new CollectorSink<>(new LinkedList<>());

        commitProducerStage.declareActive();
        revCommitMerger.declareActive();
        commitDiffCountFilter.declareActive();

        final int numDiffThreads = Math.min(Math.max(Config.threads - 4, 2), 6);
        for (int i = 0; i < numDiffThreads; i++) {
            final CommitParserStage commitParserStage = new CommitParserStage();
            commitParserStage.declareActive();
            connectPorts(revCommitDistributor.getNewOutputPort(), commitParserStage.getInputPort());
            connectPorts(commitParserStage.getOutputPort(), revCommitMerger.getNewInputPort());
        }

        connectPorts(commitProducerStage.getOutputPort(), commitMetaFilter.getInputPort());
        connectPorts(commitMetaFilter.getOutputPort(), combineConsecutiveStage.getInputPort());
        connectPorts(combineConsecutiveStage.getOutputPort(), revCommitDistributor.getInputPort());
        connectPorts(revCommitMerger.getOutputPort(), commitDiffCountFilter.getInputPort());
        connectPorts(commitDiffCountFilter.getOutputPort(), progressBarStage.getInputPort());
        connectPorts(progressBarStage.getOutputPort(), this.collectorStage.getInputPort());
    }

    public List<List<DiffEntry>> getCommits() {
        return this.collectorStage.getElements();
    }

}
