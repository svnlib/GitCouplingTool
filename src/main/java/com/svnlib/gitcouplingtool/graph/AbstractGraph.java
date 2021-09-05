package com.svnlib.gitcouplingtool.graph;

import java.util.*;

public abstract class AbstractGraph<V, E extends AbstractEdge<V>> {

    protected final Set<V>          nodes = new HashSet<>();
    protected final Map<Integer, E> edges = new HashMap<>();

    public void addVertex(final V v) {
        synchronized (this.nodes) {
            this.nodes.add(v);
        }
    }

    public E findOrCreateEdge(final V src, final V dest, final double weight) {
        final E newEdge = createEdge(src, dest, weight);

        E edge;
        synchronized (this.edges) {
            edge = this.edges.get(newEdge.hashCode());
            if (edge == null) {
                this.edges.put(newEdge.hashCode(), newEdge);
                edge = newEdge;
            }
        }
        return edge;
    }

    public Collection<V> getNodes() {
        return this.nodes;
    }

    public Collection<E> getEdges() {
        return this.edges.values();
    }

    protected abstract E createEdge(V src, V dest, double weight);

    @Override
    public String toString() {
        return "AbstractGraph{" +
               "nodes=" + this.nodes +
               ", edges=" + this.edges.values() +
               '}';
    }

}
