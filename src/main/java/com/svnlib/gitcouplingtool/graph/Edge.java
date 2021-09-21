package com.svnlib.gitcouplingtool.graph;

public class Edge implements Comparable<Edge> {

    protected final String  src;
    protected final String  dest;
    protected final double  weight;
    protected final boolean directed;

    public Edge(final String src, final String dest, final double weight, final boolean directed) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.directed = directed;
    }

    public String getSrc() {
        return this.src;
    }

    public String getDest() {
        return this.dest;
    }

    public double getWeight() {
        return this.weight;
    }

    public boolean isDirected() {
        return this.directed;
    }

    @Override
    public int compareTo(final Edge o) {
        return Double.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
        return "Edge{" +
               "src='" + this.src + '\'' +
               ", dest='" + this.dest + '\'' +
               ", weight=" + this.weight +
               ", directed=" + this.directed +
               '}';
    }

}
