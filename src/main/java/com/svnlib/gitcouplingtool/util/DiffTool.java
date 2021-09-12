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

/**
 * Tool for calculating diffs between commits.
 */
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

    /** Closes all internal components. */
    public void close() {
        this.walk.close();
        this.objectReader.close();
        this.df.close();
    }

    /**
     * Returns the diffs between the given commit and its parent.
     *
     * @param commit the commit to get the diff from
     *
     * @return a possibly empty list off {@link DiffEntry}s
     */
    public List<DiffEntry> diff(final RevCommit commit) throws IOException {
        final AbstractTreeIterator newTreeIter =
                new CanonicalTreeParser(null, this.objectReader, commit.getTree());
        final AbstractTreeIterator oldTreeIter = getParentTreeIterator(commit);

        return diff(newTreeIter, oldTreeIter);
    }

    /**
     * Returns the diffs between the given commits.
     *
     * @param commit1 the first commit
     * @param commit2 the second commit
     *
     * @return a possibly empty list off {@link DiffEntry}s
     */
    public List<DiffEntry> diff(final RevCommit commit1, final RevCommit commit2) throws IOException {
        if (commit1.getTree() == null) {
            this.walk.parseHeaders(commit1);
        }
        final AbstractTreeIterator newTreeIter = new CanonicalTreeParser(null, this.objectReader, commit1.getTree());

        if (commit2.getTree() == null) {
            this.walk.parseHeaders(commit2);
        }
        final AbstractTreeIterator oldTreeIter = new CanonicalTreeParser(null, this.objectReader, commit2.getTree());

        return diff(newTreeIter, oldTreeIter);
    }

    /**
     * Returns a list of diffs by comparing the given commit with an {@link EmptyTreeIterator}. Useful to determine all
     * files available at a given commit.
     *
     * @param commit the commit to use
     *
     * @return a possibly empty list off {@link DiffEntry}s which are all the type {@link
     * org.eclipse.jgit.diff.DiffEntry.ChangeType#ADD}.
     */
    public List<DiffEntry> diffWithEmpty(final RevCommit commit) throws IOException {
        final AbstractTreeIterator newTreeIter =
                new CanonicalTreeParser(null, this.objectReader, commit.getTree());
        final AbstractTreeIterator oldTreeIter = new EmptyTreeIterator();

        return diff(newTreeIter, oldTreeIter);
    }

    /**
     * Calculates the diffs between two {@link AbstractTreeIterator}.
     *
     * @param newTreeIter the first iterator
     * @param oldTreeIter the second iterator
     *
     * @return a possibly empty list off {@link DiffEntry}s
     */
    public List<DiffEntry> diff(final AbstractTreeIterator newTreeIter, final AbstractTreeIterator oldTreeIter) throws
                                                                                                                IOException {
        return this.df.scan(oldTreeIter, newTreeIter);
    }

    /**
     * Returns the TreeIterator of the parent commit of the given commit.
     *
     * @param commit the commit to get its parent's iterator.
     *
     * @return {@link CanonicalTreeParser} if the parent is not null or {@link EmptyTreeIterator} otherwise.
     */
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
