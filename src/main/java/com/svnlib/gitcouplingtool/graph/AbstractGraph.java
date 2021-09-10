package com.svnlib.gitcouplingtool.graph;

import java.util.*;

public abstract class AbstractGraph<E extends AbstractEdge> {

    protected final Set<String>     nodes = new HashSet<>();
    protected final Map<Integer, E> edges = new HashMap<>();

    public void addNode(final String node) {
        this.nodes.add(node);
    }

    public void putEdge(final String src, final String dest, final double weight) {
        final E edge = createEdge(src, dest, weight);
        putEdge(edge);
    }

    public void putEdge(final E edge) {
        synchronized (this.edges) {
            this.edges.put(edge.hashCode(), edge);
        }
    }

    public void addEdges(final Collection<E> edges) {
        edges.forEach(this::putEdge);
    }

    public Collection<String> getNodes() {
        return this.nodes;
    }

    public Collection<E> getEdges() {
        return this.edges.values();
    }

    public abstract E createEdge(String src, String dest, double weight);

    @Override
    public String toString() {
        return "AbstractGraph{" +
               "nodes=" + this.nodes +
               ", edges=" + this.edges.values() +
               '}';
    }

}
