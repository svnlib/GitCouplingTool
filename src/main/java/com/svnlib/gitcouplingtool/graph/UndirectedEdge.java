package com.svnlib.gitcouplingtool.graph;

public class UndirectedEdge<V> extends AbstractEdge<V> {

    public UndirectedEdge(final V src, final V dest, final double weight) {
        super(src, dest, weight);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (this.getClass() != o.getClass()) {
            return false;
        }

        final Object oSrc  = ((UndirectedEdge<?>) o).src;
        final Object oDest = ((UndirectedEdge<?>) o).dest;

        return (this.src.equals(oSrc) && this.dest.equals(oDest)) || (this.src.equals(oDest) && this.dest.equals(oSrc));
    }

    @Override
    public int hashCode() {
        return this.src.hashCode() + this.dest.hashCode();
    }

}
