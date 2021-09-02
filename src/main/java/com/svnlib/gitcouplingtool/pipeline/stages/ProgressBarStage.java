package com.svnlib.gitcouplingtool.pipeline.stages;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import teetime.stage.basic.AbstractFilter;

public class ProgressBarStage<T> extends AbstractFilter<T> {

    private final ProgressBarBuilder pbb = new ProgressBarBuilder();
    private       ProgressBar        pb;

    public ProgressBarStage() {
    }

    @Override
    protected void execute(final T element) throws Exception {
        this.pb.step();
        this.outputPort.send(element);
    }

    @Override
    protected void onStarting() {
        this.pb = this.pbb.build();
        super.onStarting();
    }

    @Override
    protected void onTerminating() {
        this.pb.close();
        super.onTerminating();
    }

    public ProgressBarBuilder builder() {
        return this.pbb;
    }

}
