package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.util.DiffTool;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.stage.basic.AbstractTransformation;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A TeeTime stage that is responsible for parsing the {@link DiffEntry}s of a commit and filtering them by the
 * configured file extensions.
 */
public class CommitParserStage extends AbstractTransformation<List<RevCommit>, List<DiffEntry>> {

    private final DiffTool diff;

    public CommitParserStage() {
        this.diff = new DiffTool();
    }

    @Override
    protected void execute(final List<RevCommit> consecutiveCommits) throws Exception {
        final LinkedList<RevCommit> commits = new LinkedList<>(consecutiveCommits);
        if (commits.size() == 1) {
            this.outputPort.send(filterDiffs(this.diff.diff(commits.get(0))));
        } else {
            final RevCommit firstCommit = commits.get(0);

            RevCommit lastCommit = commits.get(commits.size() - 1);
            if (lastCommit.getParentCount() > 0) {
                lastCommit = lastCommit.getParent(0);
            }

            this.outputPort.send(filterDiffs(this.diff.diff(firstCommit, lastCommit)));
        }
    }

    /**
     * Filters the given list of {@link DiffEntry}s for paths ending with the configured file extensions.
     *
     * @param diffs the {@link DiffEntry}s to be filtered
     *
     * @return a possibly empty list of {@link DiffEntry}s that match to the configured file extensions or the given
     * list itself, if no file extension is configured.
     */
    private List<DiffEntry> filterDiffs(final List<DiffEntry> diffs) {
        if (Config.fileTypes == null || Config.fileTypes.size() == 0) {
            return diffs;
        }

        return diffs.stream()
                    .filter(diff1 -> {
                        for (final String type : Config.fileTypes) {
                            if (diff1.getNewPath().endsWith(type)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
    }

}
