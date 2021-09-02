package com.svnlib.gitcouplingtool.model;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

public class Commit {

    private final String          hash;
    private final long            timestamp;
    private final List<DiffEntry> diffs;

    public Commit(final String hash, final long timestamp, final List<DiffEntry> diffs) {
        this.hash = hash;
        this.timestamp = timestamp;
        this.diffs = diffs;
    }

    public String getHash() {
        return this.hash;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public List<DiffEntry> getDiffs() {
        return this.diffs;
    }

    @Override
    public String toString() {
        return "Commit{" +
               "hash=" + this.hash +
               ", timestamp=" + this.timestamp +
               ", diffs=" + this.diffs +
               '}';
    }

}
