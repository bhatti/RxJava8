package com.plexobject.rx.util;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.plexobject.rx.Cancelable;
import com.plexobject.rx.OnCompletion;
import com.plexobject.rx.scheduler.Scheduler;

public class CancelableSpliterator<T> implements Spliterator<T>, Cancelable {
    private final AtomicBoolean canceled = new AtomicBoolean();
    private Spliterator<T> delegate;

    private static class ParEach<T> extends CountedCompleter<Void> {
        private static final long serialVersionUID = 1L;
        final Spliterator<T> spliterator;
        final Consumer<T> onNext;
        final long targetBatchSize;

        private ParEach(ParEach<T> parent, Spliterator<T> spliterator,
                Consumer<T> onNext, long targetBatchSize) {
            super(parent);
            this.spliterator = spliterator;
            this.onNext = onNext;
            this.targetBatchSize = targetBatchSize;
        }

        @Override
        public void compute() {
            Spliterator<T> sub;
            while (spliterator.estimateSize() > targetBatchSize
                    && (sub = spliterator.trySplit()) != null) {
                addToPendingCount(1);
                new ParEach<>(this, sub, onNext, targetBatchSize).fork();
            }
            spliterator.forEachRemaining((v) -> onNext.accept(v));
            propagateCompletion();
        }
    }

    public CancelableSpliterator(Spliterator<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (canceled.get()) {
            return false;
        }
        return delegate.tryAdvance(action);
    }

    @Override
    public Spliterator<T> trySplit() {
        return delegate.trySplit();
    }

    @Override
    public long estimateSize() {
        return delegate.estimateSize();
    }

    @Override
    public int characteristics() {
        return delegate.characteristics();
    }

    @Override
    public void cancel() {
        canceled.set(true);
    }

    @Override
    public boolean isCaneled() {
        return canceled.get();
    }

    public void parEach(Consumer<T> onNext, OnCompletion onCompletion) {
        long targetBatchSize = (estimateSize() / (ForkJoinPool
                .getCommonPoolParallelism() * 8));
        Scheduler.newNewThreadScheduler().scheduleBackgroundTask(it -> {
            new ParEach<T>(null, this, onNext, targetBatchSize).invoke();
            onCompletion.onCompleted();
        }, this);
    }

    public Stream<T> toStream() {
        return StreamSupport.stream(this, true);
    }
}
