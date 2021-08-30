package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.model.Commit;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import teetime.framework.AbstractConsumerStage;

public class CommitAnalysisStage extends AbstractConsumerStage<Commit> {

    private final ProgressBar pb = new ProgressBarBuilder()
            .setUnit(" Commits", 1)
            .showSpeed()
            .setUpdateIntervalMillis(100)
            .setStyle(ProgressBarStyle.ASCII)
            .setTaskName("Progress")
            .build();

    @Override
    protected void execute(final Commit element) {
        this.pb.step();
    }

    @Override
    protected void onTerminating() {
        this.pb.close();
        super.onTerminating();
    }

}
