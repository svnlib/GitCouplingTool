package com.svnlib.gitcouplingtool.util;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class GitUtils {

    public static Collection<String> getFilesAtCommit(final RevCommit commit) throws IOException {
        try (final DiffTool diffTool = new DiffTool()) {
            return diffTool.diffWithEmpty(commit)
                           .stream()
                           .map(DiffEntry::getNewPath)
                           .collect(Collectors.toList());
        }
    }

    /**
     * Returns the first commit to start the walk at by parsing the configured options (e.g. --from-commit, --branch)
     *
     * @return the best matching commit
     */
    public static RevCommit getFirstCommitFromConfig() throws IOException {
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

        return getRevCommitFromRef(ref);
    }

    /**
     * Get the commit at which the traversal should stop from the global config.
     *
     * @return {@code null} if nothing is configured and a specific {@link RevCommit} otherwise.
     */
    public static RevCommit getLastCommitFromConfig() throws IOException {
        String ref = null;
        if (Config.toCommit != null) {
            ref = Config.toCommit;
        } else if (Config.toTag != null) {
            ref = Config.toTag;
        }

        if (ref == null) {
            return null;
        }

        return getRevCommitFromRef(ref);
    }

    /**
     * Returns a {@link RevCommit} to a given reference like HEAD, master or 87f12dee.
     *
     * @param ref the reference
     *
     * @return the corresponding {@link RevCommit}
     */
    private static RevCommit getRevCommitFromRef(final String ref) throws IOException {
        final Repository repository = Config.git.getRepository();
        final ObjectId   objectId   = repository.resolve(ref + "^{commit}");

        try (final RevWalk walk = new RevWalk(repository)) {
            return walk.parseCommit(objectId);
        }
    }

}
