package com.plexobject.rx.util;

import java.util.Spliterator;
import java.util.function.Consumer;

public class ArraySpliterator<T> implements Spliterator<T> {
    private final Object[] array;
    private int origin; // current index, advanced on split or traversal
    private final int fence; // one past the greatest index

    public ArraySpliterator(Object[] array, int origin, int fence) {
        this.array = array;
        this.origin = origin;
        this.fence = fence;
    }

    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super T> action) {
        for (; origin < fence; origin++)
            action.accept((T) array[origin]);
    }

    @SuppressWarnings("unchecked")
    public boolean tryAdvance(Consumer<? super T> action) {
        if (origin < fence) {
            action.accept((T) array[origin]);
            origin++;
            return true;
        } else
            // cannot advance
            return false;
    }

    public Spliterator<T> trySplit() {
        int lo = origin; // divide range in half
        int mid = ((lo + fence) >>> 1) & ~1; // force midpoint to be even
        if (lo < mid) { // split out left half
            origin = mid; // reset this Spliterator's origin
            return new ArraySpliterator<>(array, lo, mid);
        } else
            // too small to split
            return null;
    }

    public long estimateSize() {
        return (long) (fence - origin);
    }

    public int characteristics() {
        return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
    }
}
