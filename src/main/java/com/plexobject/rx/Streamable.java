package com.plexobject.rx;

import java.util.stream.Stream;

public interface Streamable<T> {
    Stream<T> getStream();
}
