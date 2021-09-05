package com.svnlib.gitcouplingtool.graph;

public abstract class AbstractEdge<V> {

    protected final V      src;
    protected final V      dest;
    protected       double weight;

    public AbstractEdge(final V src, final V dest, final double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    public V getSrc() {
        return this.src;
    }

    public V getDest() {
        return this.dest;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(final double weight) {
        this.weight = weight;
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
