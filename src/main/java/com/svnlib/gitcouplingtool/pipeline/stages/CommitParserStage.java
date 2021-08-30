package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.util.DiffTool;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.stage.basic.AbstractTransformation;

import java.util.List;

public class CommitParserStage extends AbstractTransformation<RevCommit, Commit> {

    private final DiffTool diff;

    public CommitParserStage() {
        this.diff = new DiffTool();
    }

    @Override
    protected void execute(final RevCommit revCommit) throws Exception {
        final List<DiffEntry> diffs = this.diff.diff(revCommit);
        this.outputPort.send(new Commit(revCommit.getId(), revCommit.getCommitTime(), diffs));
    }

}
