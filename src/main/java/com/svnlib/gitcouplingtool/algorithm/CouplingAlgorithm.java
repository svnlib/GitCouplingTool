package com.svnlib.gitcouplingtool.algorithm;

import java.io.Writer;
import java.util.Collection;

/** A general interface for a coupling algorithm. */
public interface CouplingAlgorithm {

    /**
     * Tells the algorithm that the given {@link Collection<Artifact>} changed in one commit.
     *
     * @param artifacts the artifacts that changed in one commit
     */
    void changedArtifacts(Collection<Artifact> artifacts);

    /**
     * Perform all necessary operations to determine the final coupling graph.
     */
    void execute();

    /**
     * Export the resulting graph by writing it to the given {@link Writer}.
     *
     * @param writer the writer to write the exported graph to
     */
    void export(Writer writer) throws Exception;

}
