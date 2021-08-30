package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.pipeline.stages.CommitAnalysisStage;
import com.svnlib.gitcouplingtool.pipeline.stages.CommitFilter;
import com.svnlib.gitcouplingtool.pipeline.stages.CommitParserStage;
import com.svnlib.gitcouplingtool.pipeline.stages.CommitProducerStage;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.framework.Configuration;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

import java.io.IOException;

public class Pipeline extends Configuration {

    public Pipeline() throws IOException {

        final CommitProducerStage commitProducerStage = new CommitProducerStage();
        final CommitFilter        commitFilter        = new CommitFilter();
        final CommitAnalysisStage commitAnalysisStage = new CommitAnalysisStage();

        final Distributor<RevCommit> revCommitDistributor = new Distributor<>();
        final Merger<Commit>         revCommitMerger      = new Merger<>();

        commitProducerStage.declareActive();
        commitAnalysisStage.declareActive();
        revCommitDistributor.declareActive();
        revCommitMerger.declareActive();

        final int numDiffThreads = Math.max(Config.threads - 3, 1);
        for (int i = 0; i < numDiffThreads; i++) {
            final CommitParserStage commitParserStage = new CommitParserStage();
            commitParserStage.declareActive();
            connectPorts(revCommitDistributor.getNewOutputPort(), commitParserStage.getInputPort());
            connectPorts(commitParserStage.getOutputPort(), revCommitMerger.getNewInputPort());
        }

        connectPorts(commitProducerStage.getOutputPort(), commitFilter.getInputPort());
        connectPorts(commitFilter.getOutputPort(), revCommitDistributor.getInputPort());
        connectPorts(revCommitMerger.getOutputPort(), commitAnalysisStage.getInputPort());
    }

}
