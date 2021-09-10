package com.svnlib.gitcouplingtool.algorithm;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Objects;
import java.util.UUID;

public class Artifact {

    private final UUID   id;
    private final String originalPath;

    private String  currentPath;
    private boolean editable = true;

    public Artifact(final String originalPath) {
        this.id = UUID.randomUUID();
        this.originalPath = originalPath;
        this.currentPath = this.originalPath;
    }

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

    public UUID getId() {
        return this.id;
    }

    public synchronized String getOriginalPath() {
        return this.originalPath;
    }

    public synchronized String getCurrentPath() {
        return this.currentPath;
    }

    public synchronized boolean isEditable() {
        return this.editable;
    }

    @Override
    public synchronized boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Artifact artifact = (Artifact) o;
        return Objects.equals(this.id, artifact.id);
    }

    @Override
    public synchronized int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "Artifact{" + this.originalPath + '}';
    }

}
