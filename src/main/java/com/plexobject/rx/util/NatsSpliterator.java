package com.plexobject.rx.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This is a helper class for creating infinite natural numbers
 * 
 * @author Shahzad Bhatti
 *
 */
public class NatsSpliterator implements Spliterator<Integer> {
    private int number;

    public NatsSpliterator(int from) {
        this.number = from;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        action.accept(number++);
        return true;
    }

    @Override
    public Spliterator<Integer> trySplit() {
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

    public Stream<Integer> toStream() {
        return StreamSupport.stream(this, false);
    }
}
