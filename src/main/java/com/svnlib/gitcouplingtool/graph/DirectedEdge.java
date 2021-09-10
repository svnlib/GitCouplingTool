package com.svnlib.gitcouplingtool.graph;

import java.util.Objects;

public class DirectedEdge extends AbstractEdge {

    public DirectedEdge(final String src, final String dest, final double weight) {
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

        return this.src.equals(((DirectedEdge) o).src) && this.dest.equals(((DirectedEdge) o).dest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.src, this.dest);
    }

}
