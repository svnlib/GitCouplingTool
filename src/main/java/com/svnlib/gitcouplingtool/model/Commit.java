package com.svnlib.gitcouplingtool.model;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

public class Commit {

    private final String          hash;
    private final List<DiffEntry> diffs;

    public Commit(final String hash, final List<DiffEntry> diffs) {
        this.hash = hash;
        this.diffs = diffs;
    }

    public List<DiffEntry> getDiffs() {
        return this.diffs;
    }

    @Override
    public String toString() {
        return "Commit{" +
               "hash=" + this.hash +
               ", diffs=" + this.diffs +
               '}';
    }

}
