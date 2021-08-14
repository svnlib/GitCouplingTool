import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws IOException, GitAPIException {
        final RepoMiner miner = new RepoMiner("/Volumes/External/GitHub/git");

        final Iterable<RevCommit> commits = miner.getCommits(CommitFilter.builder().noMerges().build());

        long count = 0;
        for (final RevCommit commit : commits) {
            count++;
            if (commit.getParentCount() < 1) {
                continue;
            }

            final List<DiffEntry> diffs = miner.getDiffsFromCommit(commit);

            commit.disposeBody();
            System.out.print("\r" + count);
        }

        System.out.println("Done");
    }

}
