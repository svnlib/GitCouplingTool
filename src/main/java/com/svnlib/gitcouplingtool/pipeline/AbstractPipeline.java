package com.svnlib.gitcouplingtool.pipeline;

import teetime.framework.Configuration;
import teetime.framework.Execution;

public abstract class AbstractPipeline extends Configuration {

    public void execute() {
        final Execution<AbstractPipeline> execution = new Execution<>(this);
        execution.executeBlocking();
    }

}
