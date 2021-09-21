package com.svnlib.gitcouplingtool.algorithm;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Objects;

/**
 * An {@link Artifact} stores the original and current path of a file and a flag, whether the artifact is editable.
 */
public class Artifact {

    /**
     * The path of the corresponding file, when this artifact object was created. This is the path the file has in the
     * last commit of the current branch. e.g. in git the file was created as "test.txt" but later renamed to
     * "other.txt". But as we walk the tree of changes backwards, the original name of the artifact would be
     * "other.txt".
     */
    private final String originalPath;

    /** The path the file has at the current point of the history traversal. */
    private String currentPath;

    /**
     * Whether the traversal came across a commit in which this file was created. This means that there can not be any
     * other changes to the path.
     */
    private boolean editable = true;

    /**
     * Create an {@link Artifact} with the provided path as its original path. See {@link Artifact#originalPath}.
     *
     * @param originalPath the path to be set as the originalPath
     */
    public Artifact(final String originalPath) {
        this.originalPath = originalPath;
        this.currentPath = this.originalPath;
    }

    /**
     * Takes a {@link DiffEntry} and checks whether the artifact changed its name or was created. If the editable flag
     * was already set to {@code false}, this methode returns immediately.
     *
     * @param diffEntry the {@link DiffEntry} to parse
     */
    public synchronized void parseDiffEntry(final DiffEntry diffEntry) {
        if (!this.editable) {
            return;
        }

        switch (diffEntry.getChangeType()) {
            case RENAME:
                this.currentPath = diffEntry.getOldPath();
                break;
            case ADD:
            case COPY:
                this.editable = false;
        }
    }

    /**
     * Returns the originalPath of the artifact
     *
     * @return the originalPath
     */
    public String getOriginalPath() {
        return this.originalPath;
    }

    /**
     * Returns the currentPath the artifact has at the current state of commit traversal.
     *
     * @return the currentPath
     */
    public synchronized String getCurrentPath() {
        return this.currentPath;
    }

    /**
     * Returns whether the artifact is in an editable state or not.
     *
     * @return {@code true} if the artifact is still editable or {@code false} if the file is no longer editable
     */
    public synchronized boolean isEditable() {
        return this.editable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Artifact artifact = (Artifact) o;
        return Objects.equals(this.originalPath, artifact.originalPath);
    }

    @Override
    public int hashCode() {
        return this.originalPath.hashCode();
    }

    @Override
    public String toString() {
        return "Artifact{" + this.originalPath + '}';
    }

}
