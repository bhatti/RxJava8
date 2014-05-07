package com.plexobject.rx.util;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * This class implements spliterator interface for tuple while taking two input
 * spliterators.
 * 
 * @author Shahzad Bhatti
 *
 */
public class TupleSpliterator<A, B> extends CancelableSpliterator<Tuple> {
    public TupleSpliterator(Spliterator<A> delegate1, Spliterator<B> delegate2) {
        super(new Spliterator<Tuple>() {

            @Override
            public boolean tryAdvance(Consumer<? super Tuple> action) {
                if (delegate1.estimateSize() > delegate2.estimateSize()) {
                    return delegate1.tryAdvance(o1 -> {
                        if (delegate2.tryAdvance(o2 -> {
                            Tuple tuple = new Tuple(o1, o2);
                            action.accept(tuple);
                        })) {
                            // already called action
                        } else {
                            Tuple tuple = new Tuple(o1);
                            action.accept(tuple);
                        }
                    });
                } else {
                    return delegate2.tryAdvance(o2 -> {
                        if (delegate1.tryAdvance(o1 -> {
                            Tuple tuple = new Tuple(o1, o2);
                            action.accept(tuple);
                        })) {
                            // already called action
                        } else {
                            Tuple tuple = new Tuple(o2);
                            action.accept(tuple);
                        }
                    });
                }
            }

            @Override
            public Spliterator<Tuple> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return Math.max(delegate1.estimateSize(),
                        delegate2.estimateSize());
            }

            @Override
            public int characteristics() {
                return delegate1.characteristics()
                        & delegate2.characteristics();
            }
        });
        Objects.requireNonNull(delegate1);
        Objects.requireNonNull(delegate2);
    }
}
