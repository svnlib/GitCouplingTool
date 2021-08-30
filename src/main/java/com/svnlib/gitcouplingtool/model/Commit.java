package com.svnlib.gitcouplingtool.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;

import java.util.List;

public class Commit {

    private final ObjectId        id;
    private final long            timestamp;
    private final List<DiffEntry> diffs;

    public Commit(final ObjectId id, final long timestamp, final List<DiffEntry> diffs) {
        this.id = id;
        this.timestamp = timestamp;
        this.diffs = diffs;
    }

    public ObjectId getId() {
        return this.id;
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
               "id=" + this.id.name() +
               ", timestamp=" + this.timestamp +
               ", diffs=" + this.diffs +
               '}';
    }

}
