package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.AbstractAlgorithm;
import com.svnlib.gitcouplingtool.model.Artifact;
import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.pipeline.stages.AlgorithmStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ArtifactStoreStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ProgressBarStage;
import me.tongfei.progressbar.ProgressBarStyle;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

import java.util.Collection;

public class AnalysePipeline extends AbstractPipeline {

    public AnalysePipeline(final Collection<Commit> commits, final AbstractAlgorithm algorithm) {
        final InitialElementProducer<Commit>         producer              = new InitialElementProducer<>(commits);
        final Distributor<Commit>                    commitDistributor     = new Distributor<>();
        final Merger<Collection<Artifact>>           collectionMerger      = new Merger<>();
        final Distributor<Collection<Artifact>>      collectionDistributor = new Distributor<>();
        final Merger<Collection<Artifact>>           progressMerger        = new Merger<>();
        final ProgressBarStage<Collection<Artifact>> progressBarStage      = new ProgressBarStage<>();
        progressBarStage.builder()
                        .setUnit(" Commits", 1)
                        .showSpeed()
                        .setUpdateIntervalMillis(500)
                        .setStyle(ProgressBarStyle.ASCII)
                        .setTaskName("Performing Algorithm")
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
