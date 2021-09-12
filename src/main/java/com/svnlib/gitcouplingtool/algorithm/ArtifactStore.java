package com.svnlib.gitcouplingtool.algorithm;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An {@link ArtifactStore} has a {@link Set} of {@link Artifact}. This class has only one instance and has to be
 * initialized with {@link ArtifactStore#initialize(Collection)} and a Collection of original paths.
 */
public class ArtifactStore {

    /** The only instance of this class. */
    public static final ArtifactStore INSTANCE = new ArtifactStore();

    private final Set<Artifact> artifacts = new HashSet<>();

    private ArtifactStore() {
    }

    /**
     * Creates an {@link Artifact} for each given path.
     *
     * @param paths the paths to create an {@link Artifact} for
     */
    public void initialize(final Collection<String> paths) {
        synchronized (this.artifacts) {
            paths.stream()
                 .map(Artifact::new)
                 .forEach(this.artifacts::add);
        }
    }

    /**
     * Finds all effected {@link Artifact}s and applies the {@link DiffEntry} on them.
     *
     * @param diffs the {@link DiffEntry}s to apply
     *
     * @return the effected {@link Artifact}s
     */
    public Set<Artifact> applyDiffsOnArtifacts(final List<DiffEntry> diffs) {
        final Set<Artifact> effectedArtifacts = new HashSet<>();
        for (final DiffEntry diff : diffs) {
            final String newPath = diff.getNewPath();

            final Artifact artifact = findArtifactByPath(newPath);
            if (artifact != null) {
                artifact.parseDiffEntry(diff);
                effectedArtifacts.add(artifact);
            }
        }
        return effectedArtifacts;
    }

    /**
     * Searches the artifact set for an artifact with a current path equal to the given path.
     *
     * @param path the path to search for
     *
     * @return the corresponding {@link Artifact} or {@code null} if nothing was found
     */
    private Artifact findArtifactByPath(final String path) {
        for (final Artifact artifact : this.artifacts) {
            if (artifact.getCurrentPath().equals(path)) {
                return artifact;
            }
        }
        return null;
    }

    /**
     * Returns the set of {@link Artifact}s.
     *
     * @return the artifact set
     */
    public Set<Artifact> getArtifacts() {
        synchronized (this.artifacts) {
            return this.artifacts;
        }
    }

}
