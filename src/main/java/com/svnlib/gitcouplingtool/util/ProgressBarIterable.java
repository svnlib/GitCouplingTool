package com.svnlib.gitcouplingtool.util;

import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.wrapped.ProgressBarWrappedIterator;

import java.util.Iterator;

public class ProgressBarIterable<T> implements Iterable<T> {

    private final Iterable<T>        underlying;
    private final ProgressBarBuilder pbb;

    private ProgressBarIterable(final Iterable<T> iterable, final ProgressBarBuilder pbb) {
        final long size = iterable.spliterator().estimateSize();
        if (size != Long.MAX_VALUE) {
            pbb.setInitialMax(size);
        }

        this.underlying = iterable;
        this.pbb = pbb;
    }

    public static <T> ProgressBarIterable<T> wrap(final Iterable<T> iterable, final ProgressBarBuilder pbb) {
        return new ProgressBarIterable<T>(iterable, pbb);
    }

    @Override
    public ProgressBarWrappedIterator<T> iterator() {
        final Iterator<T> it = this.underlying.iterator();
        return new ProgressBarWrappedIterator<>(
                it,
                this.pbb.build()
        );
    }

}