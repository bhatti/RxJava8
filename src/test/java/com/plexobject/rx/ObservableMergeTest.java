package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Test;

public class ObservableMergeTest extends BaseObservableTest {

    @Test
    public void testSubscribeMerge() throws Exception {
        Observable<Integer> observable1 = Observable.from(Stream.of(1, 2, 3));
        Observable<Integer> observable2 = Observable.from(Stream.of(4, 5, 6));

        Observable<Integer> observableMerged = observable1.merge(observable2);

        initLatch(6 + 1);

        setupCallback(observableMerged, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(6, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testMergeToList() {
        List<Integer> list = Observable.from(1, 2).merge(Observable.from(3, 4))
                .toList();

        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(2), list.get(1));
        assertEquals(new Integer(3), list.get(2));
        assertEquals(new Integer(4), list.get(3));
    }

    @Test
    public void testMergeToSet() {
        Set<Integer> set = Observable.from(1, 2).merge(Observable.from(3, 4))
                .merge(Observable.just(3)).toSet();
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
    }

}
