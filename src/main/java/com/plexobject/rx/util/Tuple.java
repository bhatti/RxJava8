package com.plexobject.rx.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * The tuple class stores objects of different types
 * 
 * @author Shahzad Bhatti
 *
 */
public class Tuple implements Serializable, Iterable<Object>,
        IntFunction<Object> {
    private static final long serialVersionUID = 1L;
    private final List<Object> objects = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Tuple(Object... objs) {
        Objects.requireNonNull(objs);
        for (Object o : objs) {
            if (o instanceof Collection) {
                Collection<Object> collection = (Collection<Object>) o;
                this.objects.addAll(collection);
            } else if (o.getClass().isArray()) {
                Object[] arr = (Object[]) o;
                this.objects.addAll(Arrays.asList(arr));
            } else if (o instanceof Tuple) {
                Tuple tuple = (Tuple) o;
                this.objects.addAll(tuple.objects);
            } else {
                this.objects.add(o);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((objects == null) ? 0 : objects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple other = (Tuple) obj;
        if (objects == null) {
            if (other.objects != null)
                return false;
        } else if (!objects.equals(other.objects))
            return false;
        return true;
    }

    public <T> T getFirst() {
        return get(0);
    }

    public <T> T getSecond() {
        return get(1);
    }

    public <T> T getThird() {
        return get(1);
    }

    public <T> T getLast() {
        return get(objects.size() - 1);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) objects.get(index);
    }

    @Override
    public Iterator<Object> iterator() {
        return objects.iterator();
    }

    @Override
    public Object apply(int index) {
        return get(index);
    }

    @Override
    public String toString() {
        return objects.toString();
    }

}
