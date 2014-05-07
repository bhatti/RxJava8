package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.plexobject.rx.util.Tuple;

public class ObservableMergeZipTest extends BaseObservableTest {

    @Test
    public void testSubscribeMerge() throws Exception {
        Observable<Integer> observable1 = Observable.from(1, 2, 3);
        Observable<Integer> observable2 = Observable.from(4, 5, 6);

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

    @Test
    public void testSubscribeZip() throws Exception {
        Observable<String> observable1 = Observable.from("One", "Two", "Three");
        Observable<Integer> observable2 = Observable.from(1, 2, 3);
        List<Tuple> expectedTuples = Arrays.asList(new Tuple("One", 1),
                new Tuple("Two", 2), new Tuple("Three", 3));

        Observable<Tuple> observableMerged = observable1.zip(observable2);

        initLatch(3 + 1);
        final List<Tuple> returnedTuples = new ArrayList<>();
        setupCallback(observableMerged, (t) -> returnedTuples.add(t), true);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(3, returnedTuples.size());
        assertEquals(3, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
        assertEquals(expectedTuples, returnedTuples);
    }

    @Test
    public void testSubscribeZipWithUnEvenSize() throws Exception {
        Observable<String> observable1 = Observable.from("One", "Two", "Three");
        Observable<Integer> observable2 = Observable.from(1, 2, 3, 4);
        List<Tuple> expectedTuples = Arrays.asList(new Tuple("One", 1),
                new Tuple("Two", 2), new Tuple("Three", 3), new Tuple(4));

        Observable<Tuple> observableMerged = observable1.zip(observable2);

        initLatch(3 + 1);
        final List<Tuple> returnedTuples = new ArrayList<>();
        setupCallback(observableMerged, (t) -> returnedTuples.add(t), true);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(4, returnedTuples.size());
        assertEquals(4, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
        assertEquals(expectedTuples, returnedTuples);
    }

    @Test
    public void testSubscribeZipWithEmptyList() throws Exception {
        Observable<Integer> observable1 = Observable.from();
        Observable<String> observable2 = Observable.from("One", "Two", "Three");
        List<Tuple> expectedTuples = Arrays.asList(new Tuple("One"), new Tuple(
                "Two"), new Tuple("Three"));

        Observable<Tuple> observableMerged = observable1.zip(observable2);

        initLatch(3 + 1);
        final List<Tuple> returnedTuples = new ArrayList<>();
        setupCallback(observableMerged, (t) -> returnedTuples.add(t), true);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(3, returnedTuples.size());
        assertEquals(3, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
        assertEquals(expectedTuples, returnedTuples);
    }
}
