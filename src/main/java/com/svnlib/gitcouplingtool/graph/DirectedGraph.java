package com.svnlib.gitcouplingtool.graph;

public class DirectedGraph extends AbstractGraph<DirectedEdge> {

    @Override
    public DirectedEdge createEdge(final String src, final String dest, final double weight) {
        return new DirectedEdge(src, dest, weight);
    }

}
