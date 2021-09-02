package com.svnlib.gitcouplingtool.util;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class DiffTool {

    private final RevWalk       walk;
    private final ObjectReader  objectReader;
    private final DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());

    public DiffTool() {
        final Repository repository = Config.git.getRepository();

        this.walk = new RevWalk(repository);
        this.objectReader = repository.newObjectReader();
        this.df.setRepository(repository);
        this.df.setDiffComparator(RawTextComparator.DEFAULT);
        this.df.setDetectRenames(Config.followRenames);
    }

    public List<DiffEntry> diff(final RevCommit commit) throws IOException {

        final AbstractTreeIterator newTreeIter =
                new CanonicalTreeParser(null, this.objectReader, commit.getTree());
        final AbstractTreeIterator oldTreeIter = getParentTreeIterator(commit);

        return diff(newTreeIter, oldTreeIter);
    }

    public List<DiffEntry> diffWithEmpty(final RevCommit commit) throws IOException {
        final AbstractTreeIterator newTreeIter =
                new CanonicalTreeParser(null, this.objectReader, commit.getTree());
        final AbstractTreeIterator oldTreeIter = new EmptyTreeIterator();

        return diff(newTreeIter, oldTreeIter);
    }

    public List<DiffEntry> diff(final AbstractTreeIterator newTreeIter, final AbstractTreeIterator oldTreeIter) throws
                                                                                                                IOException {
        return this.df.scan(oldTreeIter, newTreeIter);
    }

    private AbstractTreeIterator getParentTreeIterator(final RevCommit commit) throws IOException {
        if (commit.getParentCount() == 0) {
            return new EmptyTreeIterator();
        }

        final RevCommit parentCommit = commit.getParent(0);
        if (parentCommit.getTree() == null) {
            this.walk.parseHeaders(parentCommit);
        }
        return new CanonicalTreeParser(null, this.objectReader, parentCommit.getTree());
    }

}
