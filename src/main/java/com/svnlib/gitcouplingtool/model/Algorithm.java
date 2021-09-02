package com.svnlib.gitcouplingtool.model;

import com.svnlib.gitcouplingtool.algorithm.AbstractAlgorithm;
import com.svnlib.gitcouplingtool.algorithm.UndirectedRawCounting;

public enum Algorithm {
    URC("Undirected Raw Counting", new UndirectedRawCounting()),
    DRC("Directed Raw Counting", null);

    private final String            name;
    private final AbstractAlgorithm algorithm;

    Algorithm(final String name, final AbstractAlgorithm algorithm) {
        this.name = name;
        this.algorithm = algorithm;
    }

    public AbstractAlgorithm getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
