package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.graph.Edge;

import java.util.Collection;
import java.util.List;

/** An implementation of Directed Raw Counting utilizing {@link CountingAlgorithm}. */
public class DirectedRawCounting extends CountingAlgorithm {

    /**
     * Initializes the algorithm by creating the counting matrix and the indices.
     *
     * @param artifacts the artifacts to be indexed
     */
    public DirectedRawCounting(final Collection<Artifact> artifacts) {
        super(artifacts);
    }

    @Override
    protected Collection<Edge> createEdges(final Artifact a, final Artifact b, final int aCount, final int bCount,
                                           final int commonCount) {
        return List.of(new Edge(a.getOriginalPath(),
                                b.getOriginalPath(),
                                ((double) commonCount) / aCount,
                                true),
                       new Edge(b.getOriginalPath(),
                                a.getOriginalPath(),
                                ((double) commonCount) / bCount,
                                true));
    }

}
