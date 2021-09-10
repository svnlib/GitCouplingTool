package com.svnlib.gitcouplingtool.graph;

public abstract class AbstractEdge implements Comparable<AbstractEdge> {

    protected final String src;
    protected final String dest;
    protected final double weight;

    public AbstractEdge(final String src, final String dest, final double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
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

    @Override
    public int compareTo(final AbstractEdge o) {
        return Double.compare(this.weight, o.weight);
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return "AbstractEdge{" +
               "src=" + this.src +
               ", dest=" + this.dest +
               ", weight=" + this.weight +
               '}';
    }

}
