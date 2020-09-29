package j$.util.stream;

import j$.util.Spliterator;
import java.util.Iterator;

/* renamed from: j$.util.stream.l1  reason: case insensitive filesystem */
public interface CLASSNAMEl1 extends AutoCloseable {
    void close();

    boolean isParallel();

    Iterator iterator();

    CLASSNAMEl1 onClose(Runnable runnable);

    CLASSNAMEl1 parallel();

    CLASSNAMEl1 sequential();

    Spliterator spliterator();

    CLASSNAMEl1 unordered();
}
