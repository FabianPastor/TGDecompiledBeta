package j$.util.stream;

import j$.util.u;
import java.util.Iterator;

/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
public interface CLASSNAMEg extends AutoCloseable {
    void close();

    boolean isParallel();

    Iterator iterator();

    CLASSNAMEg onClose(Runnable runnable);

    CLASSNAMEg parallel();

    CLASSNAMEg sequential();

    u spliterator();

    CLASSNAMEg unordered();
}
