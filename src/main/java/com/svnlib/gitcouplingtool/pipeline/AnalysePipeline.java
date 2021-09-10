package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.CouplingAlgorithm;
import com.svnlib.gitcouplingtool.pipeline.stages.AlgorithmStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ArtifactStoreStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ProgressBarStage;
import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import org.eclipse.jgit.diff.DiffEntry;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

import java.util.Collection;
import java.util.List;

public class AnalysePipeline extends AbstractPipeline {

    public AnalysePipeline(final List<List<DiffEntry>> commits, final CouplingAlgorithm algorithm) {
        final InitialElementProducer<List<DiffEntry>> producer              = new InitialElementProducer<>(commits);
        final Distributor<List<DiffEntry>>            commitDistributor     = new Distributor<>();
        final Merger<Collection<Artifact>>            collectionMerger      = new Merger<>();
        final Distributor<Collection<Artifact>>       collectionDistributor = new Distributor<>();
        final Merger<Collection<Artifact>>            progressMerger        = new Merger<>();
        final ProgressBarStage<Collection<Artifact>>  progressBarStage      = new ProgressBarStage<>();
        ProgressBarUtils.addToBuilder(
                                progressBarStage.builder(),
                                "Performing Algorithm",
                                "Commits")
                        .setInitialMax(commits.size());

        for (int i = 0; i < Math.max((Config.threads - 4) / 2, 1); i++) {
            final ArtifactStoreStage artifactStoreStage = new ArtifactStoreStage();
            artifactStoreStage.declareActive();
            connectPorts(commitDistributor.getNewOutputPort(), artifactStoreStage.getInputPort());
            connectPorts(artifactStoreStage.getOutputPort(), collectionMerger.getNewInputPort());
        }
        for (int i = 0; i < Math.max((Config.threads - 4) / 2, 1); i++) {
            final AlgorithmStage algorithmStage = new AlgorithmStage(algorithm);
            algorithmStage.declareActive();
            connectPorts(collectionDistributor.getNewOutputPort(), algorithmStage.getInputPort());
            connectPorts(algorithmStage.getOutputPort(), progressMerger.getNewInputPort());
        }

        producer.declareActive();
        collectionMerger.declareActive();
        progressMerger.declareActive();

        connectPorts(producer.getOutputPort(), commitDistributor.getInputPort());
        connectPorts(collectionMerger.getOutputPort(), collectionDistributor.getInputPort());
        connectPorts(progressMerger.getOutputPort(), progressBarStage.getInputPort());
    }

}
