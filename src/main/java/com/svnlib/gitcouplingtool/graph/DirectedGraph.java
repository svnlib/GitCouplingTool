package com.svnlib.gitcouplingtool.graph;

public class DirectedGraph<V> extends AbstractGraph<V, DirectedEdge<V>> {

    @Override
    protected DirectedEdge<V> createEdge(final V src, final V dest, final double weight) {
        return new DirectedEdge<>(src, dest, weight);
    }

}
