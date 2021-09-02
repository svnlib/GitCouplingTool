package com.svnlib.gitcouplingtool.store;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.model.Artifact;
import com.svnlib.gitcouplingtool.model.Commit;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtifactStore {

    public static final ArtifactStore INSTANCE = new ArtifactStore();

    private final Set<Artifact> editableArtifacts = new HashSet<>();
    private final Set<Artifact> artifacts         = new HashSet<>();

    private ArtifactStore() {
    }

    public void initialize(final List<DiffEntry> diffs) {
        for (final DiffEntry diff : diffs) {
            final Artifact artifact = new Artifact(diff);
            this.editableArtifacts.add(artifact);
            Config.algorithm.getAlgorithm().addArtifact(artifact);
        }
    }

    public Set<Artifact> parseCommit(final Commit commit) {
        final Set<Artifact> effectedArtifacts = new HashSet<>();
        for (final DiffEntry diff : commit.getDiffs()) {
            final String newPath = diff.getNewPath();

            final Artifact artifact = getArtifactByPath(newPath);
            if (artifact != null) {
                artifact.parseDiffEntry(diff);
                effectedArtifacts.add(artifact);
                if (!artifact.isEditable()) {
                    this.editableArtifacts.remove(artifact);
                    this.artifacts.add(artifact);
                }
            }
        }
        return effectedArtifacts;
    }

    public Artifact getArtifactByPath(final String path) {
        for (final Artifact artifact : this.editableArtifacts) {
            if (artifact.getCurrentPath().equals(path)) {
                return artifact;
            }
        }
        return null;
    }

    public Set<Artifact> getArtifacts() {
        return this.artifacts;
    }

}
