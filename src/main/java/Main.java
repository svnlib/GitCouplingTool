import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws IOException, GitAPIException {
        final RepoMiner miner = new RepoMiner(args[0]);

        final Iterable<RevCommit> commits = miner.getCommits(CommitFilter.builder().noMerges().build());

        long count            = 0;
        long timeWas          = System.currentTimeMillis();
        int  commitsPerSecond = 0;
        for (final RevCommit commit : commits) {
            count++;
            if (commit.getParentCount() < 1) {
                continue;
            }

            final List<DiffEntry> diffs = miner.getDiffsFromCommit(commit);

            commit.disposeBody();
            if (count % 10 == 0) {
                final long   timeIs  = System.currentTimeMillis();
                final double seconds = (timeIs - timeWas) / 1000.0;
                commitsPerSecond -= (commitsPerSecond - (int) (10 / seconds)) >> 8;
                System.out.printf("\r%.1fk commits read [%d commits/s]", count / 1000.0, commitsPerSecond);
                timeWas = timeIs;
            }
        }

        System.out.println("Done");
    }

}
