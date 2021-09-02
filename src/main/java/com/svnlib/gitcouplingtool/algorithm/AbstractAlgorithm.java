package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.model.Artifact;

import java.io.Writer;
import java.util.List;

public abstract class AbstractAlgorithm {

    public abstract void addArtifact(final Artifact artifact);

    public abstract void addChangedArtifacts(final List<Artifact> artifacts);

    public abstract void exportGraph(final Writer writer);

}
