package com.plexobject.rx.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RangeSpliterator implements Spliterator<Integer> {
    private int from; // current index, advanced on split or traversal
    private final int to; // one past the greatest index

    public RangeSpliterator(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void forEachRemaining(Consumer<? super Integer> action) {
        for (; from < to; from++) {
            action.accept(from);
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        if (from < to) {
            action.accept(from);
            from++;
            return true;
        } else {
            // cannot advance
            return false;
        }
    }

    @Override
    public Spliterator<Integer> trySplit() {
        int lo = from; // divide range in half
        int mid = ((lo + to) >>> 1) & ~1; // force midpoint to be even
        if (lo < mid) { // split out left half
            from = mid; // reset this Spliterator's from
            return new RangeSpliterator(lo, mid);
        } else {
            // too small to split
            return null;
        }
    }

    @Override
    public long estimateSize() {
        return (long) (to - from);
    }

    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }

    public Stream<Integer> toStream() {
        return StreamSupport.stream(this, true);
    }
}
