package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.lib.ObjectId;
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
        final ObjectId   objectId   = repository.resolve(getHeadRefFromConfig() + "^{commit}");

        this.walk = new RevWalk(repository);
        this.walk.markStart(this.walk.parseCommit(objectId));

        this.lastCommit = getLastCommitFromConfig();
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
        this.workCompleted();
    }

    private String getHeadRefFromConfig() {
        if (Config.fromCommit != null) {
            return Config.fromCommit;
        }
        if (Config.fromTag != null) {
            return Config.fromTag;
        }
        if (Config.branch != null) {
            return Config.branch;
        }
        throw new IllegalArgumentException("No initial reference given");
    }

    private RevCommit getLastCommitFromConfig() throws IOException {
        final Repository repository = Config.git.getRepository();
        String           ref        = null;
        if (Config.toCommit != null) {
            ref = Config.toCommit;
        } else if (Config.toTag != null) {
            ref = Config.toTag;
        }

        if (ref == null) {
            return null;
        }

        final ObjectId objectId = repository.resolve(ref + "^{commit}");
        final RevWalk  walk     = new RevWalk(repository);

        final RevCommit commit = walk.parseCommit(objectId);
        walk.close();
        return commit;
    }

}
