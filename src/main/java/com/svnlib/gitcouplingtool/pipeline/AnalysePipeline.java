package com.svnlib.gitcouplingtool.pipeline;

import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.pipeline.stages.CommitAnalysisStage;
import com.svnlib.gitcouplingtool.pipeline.stages.ProgressBarStage;
import me.tongfei.progressbar.ProgressBarStyle;
import teetime.stage.InitialElementProducer;

import java.util.Collection;

public class AnalysePipeline extends AbstractPipeline {

    public AnalysePipeline(final Collection<Commit> commits) {
        final InitialElementProducer<Commit> producer         = new InitialElementProducer<>(commits);
        final CommitAnalysisStage            analysisStage    = new CommitAnalysisStage();
        final ProgressBarStage<Commit>       progressBarStage = new ProgressBarStage<>();
        progressBarStage.builder()
                        .setUnit(" Commits", 1)
                        .showSpeed()
                        .setUpdateIntervalMillis(500)
                        .setStyle(ProgressBarStyle.ASCII)
                        .setTaskName("Performing Algorithm")
                        .setInitialMax(commits.size());

        connectPorts(producer.getOutputPort(), analysisStage.getInputPort());
        connectPorts(analysisStage.getOutputPort(), progressBarStage.getInputPort());
    }

}
