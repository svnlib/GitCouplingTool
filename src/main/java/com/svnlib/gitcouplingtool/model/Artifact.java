package com.svnlib.gitcouplingtool.model;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Objects;
import java.util.UUID;

public class Artifact {

    private final UUID   id;
    private final String originalPath;

    private String  currentPath;
    private int     changeCount = 0;
    private boolean editable    = true;

    public Artifact(final DiffEntry diffEntry) {
        this.id = UUID.randomUUID();
        this.originalPath = diffEntry.getNewPath();
        this.currentPath = this.originalPath;
    }

    public void parseDiffEntry(final DiffEntry diffEntry) {
        if (!this.editable) {
            return;
        }

        this.changeCount++;
        switch (diffEntry.getChangeType()) {
            case RENAME:
                this.currentPath = diffEntry.getOldPath();
                break;
            case ADD:
            case COPY:
                this.editable = false;
        }
    }

    public String getOriginalPath() {
        return this.originalPath;
    }

    public String getCurrentPath() {
        return this.currentPath;
    }

    public int getChangeCount() {
        return this.changeCount;
    }

    public boolean isEditable() {
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
        return Objects.equals(this.id, artifact.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "Artifact{" + this.originalPath + '}';
    }

}
