package com.svnlib.gitcouplingtool.parsing;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CommitParser extends Thread {

    private final BlockingQueue<RevCommit> inputQueue;

    private final ObjectReader        reader;
    private final CanonicalTreeParser treeParser       = new CanonicalTreeParser();
    private final CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();

    private final RenameDetector renameDetector;

    public CommitParser(final BlockingQueue<RevCommit> inputQueue) {
        final Repository repository = Config.git.getRepository();
        this.reader = repository.newObjectReader();
        this.inputQueue = inputQueue;
        this.renameDetector = new RenameDetector(repository);
    }

    @Override
    public void run() {
        while (!isInterrupted() || !this.inputQueue.isEmpty()) {
            try {
                final RevCommit revCommit = this.inputQueue.take();
                if (revCommit.getParentCount() != 1) {
                    continue;
                }
                try {
                    final List<DiffEntry> diff = diff(revCommit);
                    //System.out.println(diff);
                } catch (final GitAPIException | IOException e) {
                    e.printStackTrace();
                }
            } catch (final InterruptedException e) {
                break;
            }
        }
    }

    private List<DiffEntry> diff(final RevCommit commit) throws GitAPIException, IOException {
        this.treeParser.reset(this.reader, commit.getTree());
        this.parentTreeParser.reset(this.reader, commit.getParent(0).getTree());

        final List<DiffEntry> diffs = Config.git.diff()
                                                .setNewTree(this.treeParser)
                                                .setOldTree(this.parentTreeParser)
                                                .call();

        if (Config.followRenames) {
            this.renameDetector.reset();
            this.renameDetector.addAll(diffs);
            return this.renameDetector.compute();
        } else {
            return diffs;
        }
    }

}
