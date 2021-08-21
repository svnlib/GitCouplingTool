package com.svnlib.gitcouplingtool.parsing;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.util.ProgressBarIterable;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommitReader extends Thread {

    private static final ProgressBarBuilder PBB = new ProgressBarBuilder()
            .setUnit(" Commits", 1)
            .showSpeed()
            .setUpdateIntervalMillis(100)
            .setStyle(ProgressBarStyle.ASCII);

    private final RevWalk                  walk;
    private final RevCommit                head;
    private final BlockingQueue<RevCommit> queue =
            new LinkedBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 2);

    public CommitReader() throws IOException {
        final Repository repository = Config.git.getRepository();
        final ObjectId   objectId   = repository.resolve("HEAD^{commit}");

        this.walk = new RevWalk(repository);
        this.head = this.walk.parseCommit(objectId);
        this.walk.setRevFilter(new CommitFilter());

        reset();
    }

    public BlockingQueue<RevCommit> getQueue() {
        return this.queue;
    }

    @Override
    public void run() {
        final long count = countCommits();

        PBB.setTaskName("Processing commits")
           .setInitialMax(count);

        // Add the head commit too
        try {
            try {
                if (this.walk.getRevFilter().include(this.walk, this.head)) {
                    this.queue.put(this.head);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } catch (final InterruptedException ignored) {
            return;
        }

        for (final RevCommit revCommit : ProgressBarIterable.wrap(this.walk, PBB)) {
            try {
                this.queue.put(revCommit);
            } catch (final InterruptedException e) {
                break;
            }
        }

        this.walk.close();
    }

    private long countCommits() {
        long count = 0;

        PBB.setTaskName("Counting commits");
        for (final RevCommit ignored : ProgressBarIterable.wrap(this.walk, PBB)) {
            count++;
        }
        reset();
        return count;
    }

    private void reset() {
        this.walk.reset();
        try {
            this.walk.markStart(this.head);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
