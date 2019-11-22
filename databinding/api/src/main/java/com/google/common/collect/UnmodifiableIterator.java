package com.google.common.collect;

import java.util.Iterator;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
    /** Constructor for use by subclasses. */
    protected UnmodifiableIterator() {}

    /**
     * Guaranteed to throw an exception and leave the underlying data unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
