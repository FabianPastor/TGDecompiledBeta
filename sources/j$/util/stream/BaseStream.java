package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.BaseStream;
import java.util.Iterator;

public interface BaseStream<T, S extends BaseStream<T, S>> extends AutoCloseable {
    void close();

    boolean isParallel();

    Iterator<T> iterator();

    S onClose(Runnable runnable);

    S parallel();

    S sequential();

    Spliterator<T> spliterator();

    S unordered();
}
