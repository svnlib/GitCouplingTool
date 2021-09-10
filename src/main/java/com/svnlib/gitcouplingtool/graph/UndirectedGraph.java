package com.svnlib.gitcouplingtool.graph;

public class UndirectedGraph extends AbstractGraph<UndirectedEdge> {

    @Override
    public UndirectedEdge createEdge(final String src, final String dest, final double weight) {
        return new UndirectedEdge(src, dest, weight);
    }

}
