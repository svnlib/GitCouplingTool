package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.diff.DiffEntry;
import teetime.stage.basic.AbstractFilter;

import java.util.List;

public class CommitDiffCountFilter extends AbstractFilter<List<DiffEntry>> {

    @Override
    protected void execute(final List<DiffEntry> diffs) {
        if (Config.excludeLargeCommits && diffs.size() >= Config.largeThreshold) {
            return;
        }
        if (diffs.size() == 0) {
            return;
        }

        this.outputPort.send(diffs);
    }

}
