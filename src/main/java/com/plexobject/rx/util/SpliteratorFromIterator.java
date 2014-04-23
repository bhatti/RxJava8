package com.plexobject.rx.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SpliteratorFromIterator<T> implements Spliterator<T> {
    private final Iterator<T> it;

    public SpliteratorFromIterator(Iterator<T> it) {
        this.it = it;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (it.hasNext()) {
            action.accept(it.next());
        }
        return it.hasNext();
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE;
    }

    public Stream<T> toStream() {
        return StreamSupport.stream(this, false);
    }
}
