package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.model.Commit;
import teetime.stage.basic.AbstractFilter;

public class CommitDiffCountFilter extends AbstractFilter<Commit> {

    @Override
    protected void execute(final Commit element) {
        if (Config.excludeLargeCommits && element.getDiffs().size() >= Config.largeThreshold) {
            return;
        }
        if (element.getDiffs().size() == 0) {
            return;
        }

        this.outputPort.send(element);
    }

}
