package com.svnlib.gitcouplingtool.algorithm;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtifactStore {

    public static final ArtifactStore INSTANCE = new ArtifactStore();

    private final Set<Artifact> artifacts = new HashSet<>();

    private ArtifactStore() {
    }

    public void initialize(final Collection<String> paths) {
        synchronized (this.artifacts) {
            paths.stream()
                 .map(Artifact::new)
                 .forEach(this.artifacts::add);
        }
    }

    public Set<Artifact> parseDiffs(final List<DiffEntry> diffs) {
        final Set<Artifact> effectedArtifacts = new HashSet<>();
        for (final DiffEntry diff : diffs) {
            final String newPath = diff.getNewPath();

            final Artifact artifact = getArtifactByPath(newPath);
            if (artifact != null) {
                artifact.parseDiffEntry(diff);
                effectedArtifacts.add(artifact);
            }
        }
        return effectedArtifacts;
    }

    private Artifact getArtifactByPath(final String path) {
        for (final Artifact artifact : this.artifacts) {
            if (artifact.getCurrentPath().equals(path)) {
                return artifact;
            }
        }
        return null;
    }

    public Set<Artifact> getArtifacts() {
        synchronized (this.artifacts) {
            return this.artifacts;
        }
    }

}
