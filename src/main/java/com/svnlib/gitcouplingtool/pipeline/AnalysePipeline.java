package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.CouplingAlgorithm;
import com.svnlib.gitcouplingtool.pipeline.stages.AlgorithmStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ProgressBarStage;
import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import org.eclipse.jgit.diff.DiffEntry;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

import java.util.Collection;
import java.util.List;

/** Pipeline for performing the algorithm on a collection of diff, as well as exporting the resulting graph afterwards. */
public class AnalysePipeline extends AbstractPipeline {

    public AnalysePipeline(final List<List<DiffEntry>> commits, final CouplingAlgorithm algorithm) {
        final InitialElementProducer<List<DiffEntry>> producer          = new InitialElementProducer<>(commits);
        final Distributor<List<DiffEntry>>            commitDistributor = new Distributor<>();
        final Merger<Collection<Artifact>>            collectionMerger  = new Merger<>();
        final ProgressBarStage<Collection<Artifact>>  progressBarStage  = new ProgressBarStage<>();
        ProgressBarUtils.addToBuilder(
                                progressBarStage.builder(),
                                "Performing Algorithm",
                                "Commits")
                        .setInitialMax(commits.size());

        for (int i = 0; i < Math.max((Config.threads - 2), 1); i++) {
            final AlgorithmStage algorithmStage = new AlgorithmStage(algorithm);
            algorithmStage.declareActive();
            connectPorts(commitDistributor.getNewOutputPort(), algorithmStage.getInputPort());
            connectPorts(algorithmStage.getOutputPort(), collectionMerger.getNewInputPort());
        }

        producer.declareActive();
        collectionMerger.declareActive();

        connectPorts(producer.getOutputPort(), commitDistributor.getInputPort());
        connectPorts(collectionMerger.getOutputPort(), progressBarStage.getInputPort());
    }

}
