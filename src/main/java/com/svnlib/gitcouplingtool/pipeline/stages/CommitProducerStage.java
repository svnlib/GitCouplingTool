package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.util.GitUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import teetime.framework.AbstractProducerStage;

import java.io.IOException;

public class CommitProducerStage extends AbstractProducerStage<RevCommit> {

    private final RevCommit lastCommit;
    private final RevWalk   walk;

    public CommitProducerStage() throws IOException {
        final Repository repository = Config.git.getRepository();

        this.lastCommit = GitUtils.getLastCommitFromConfig();
        final RevCommit firstCommit = GitUtils.getFirstCommitFromConfig();

        this.walk = new RevWalk(repository);
        this.walk.markStart(firstCommit);
    }

    @Override
    protected void execute() {
        for (final RevCommit revCommit : this.walk) {
            this.outputPort.send(revCommit);

            if (this.lastCommit != null && this.lastCommit.equals(revCommit)) {
                break;
            }
        }
        this.walk.dispose();
        this.walk.close();
        this.workCompleted();
    }

}
