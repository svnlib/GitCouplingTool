package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.store.ArtifactStore;
import com.svnlib.gitcouplingtool.util.DiffTool;
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

        this.lastCommit = getLastCommitFromConfig();
        final RevCommit firstCommit = getFirstCommitFromConfig();

        this.walk = new RevWalk(repository);
        this.walk.markStart(firstCommit);

        initializeArtifactStore(firstCommit);
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

    /**
     * Returns the first commit to start the walk at by parsing the configured options (e.g. --from-commit, --branch)
     *
     * @return the best matching commit
     */
    private RevCommit getFirstCommitFromConfig() throws IOException {
        final Repository repository = Config.git.getRepository();

        String ref = null;
        if (Config.fromCommit != null) {
            ref = Config.fromCommit;
        } else if (Config.fromTag != null) {
            ref = Config.fromTag;
        } else if (Config.branch != null) {
            ref = Config.branch;
        }

        if (ref == null) {
            ref = "HEAD";
        }

        final ObjectId objectId = repository.resolve(ref + "^{commit}");
        final RevWalk  walk     = new RevWalk(repository);

        final RevCommit commit = walk.parseCommit(objectId);
        walk.close();
        return commit;
    }

    private RevCommit getLastCommitFromConfig() throws IOException {
        final Repository repository = Config.git.getRepository();

        String ref = null;
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

    private void initializeArtifactStore(final RevCommit initialCommit) throws IOException {
        final DiffTool diffTool = new DiffTool();
        ArtifactStore.INSTANCE.initialize(diffTool.diffWithEmpty(initialCommit));
    }

}
