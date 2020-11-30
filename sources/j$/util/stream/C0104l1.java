package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;

/* renamed from: j$.util.stream.l1  reason: case insensitive filesystem */
public interface CLASSNAMEl1<T, S extends CLASSNAMEl1<T, S>> extends AutoCloseable {
    void close();

    boolean isParallel();

    Iterator iterator();

    CLASSNAMEl1 onClose(Runnable runnable);

    CLASSNAMEl1 parallel();

    CLASSNAMEl1 sequential();

    Spliterator spliterator();

    CLASSNAMEl1 unordered();
}
