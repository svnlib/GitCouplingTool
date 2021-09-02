package com.svnlib.gitcouplingtool.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractGraph<V, E extends AbstractEdge<V>> {

    protected final Set<V>          vertex = new HashSet<>();
    protected final Map<Integer, E> edges  = new HashMap<>();

    public void addVertex(final V v) {
        synchronized (this.vertex) {
            this.vertex.add(v);
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

    protected abstract E createEdge(V src, V dest, double weight);
    
    @Override
    public String toString() {
        return "AbstractGraph{" +
               "vertex=" + this.vertex +
               ", edges=" + this.edges.values() +
               '}';
    }

}
