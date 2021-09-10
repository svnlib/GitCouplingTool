package com.svnlib.gitcouplingtool.util;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GitUtils {

    public static Collection<String> getFilesAtCommit(final RevCommit commit) throws IOException {
        final DiffTool diffTool = new DiffTool();
        final List<String> paths =
                diffTool.diffWithEmpty(commit).stream().map(DiffEntry::getNewPath).collect(Collectors.toList());
        diffTool.close();
        return paths;
    }

    /**
     * Returns the first commit to start the walk at by parsing the configured options (e.g. --from-commit, --branch)
     *
     * @return the best matching commit
     */
    public static RevCommit getFirstCommitFromConfig() throws IOException {
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

    public static RevCommit getLastCommitFromConfig() throws IOException {
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

}
