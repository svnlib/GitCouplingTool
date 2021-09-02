package com.svnlib.gitcouplingtool.graph;

public class UndirectedGraph<V> extends AbstractGraph<V, UndirectedEdge<V>> {

    @Override
    protected UndirectedEdge<V> createEdge(final V src, final V dest, final double weight) {
        return new UndirectedEdge<>(src, dest, weight);
    }

}
