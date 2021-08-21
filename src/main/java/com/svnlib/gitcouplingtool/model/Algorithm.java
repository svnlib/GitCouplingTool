package com.svnlib.gitcouplingtool.model;

public enum Algorithm {
    URC("Undirected Raw Counting"),
    DRC("Directed Raw Counting");

    private final String name;

    Algorithm(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
