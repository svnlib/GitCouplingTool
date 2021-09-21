package com.svnlib.gitcouplingtool.graph;

import java.util.*;

public class Graph {

    protected final Set<String> nodes = new HashSet<>();
    protected final List<Edge>  edges = new LinkedList<>();

    public void addNode(final String node) {
        this.nodes.add(node);
    }

    public void addEdge(final Edge edge) {
        this.edges.add(edge);
    }

    public Collection<String> getNodes() {
        return this.nodes;
    }

    public Collection<Edge> getEdges() {
        return this.edges;
    }

    @Override
    public String toString() {
        return "AbstractGraph{" +
               "nodes=" + this.nodes +
               ", edges=" + this.edges +
               '}';
    }

}
