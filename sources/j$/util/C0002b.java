package j$.util;

import j$.lang.e;
import j$.util.function.Consumer;
import j$.util.function.y;
import j$.util.stream.Stream;

/* renamed from: j$.util.b  reason: case insensitive filesystem */
public interface CLASSNAMEb extends e {
    void forEach(Consumer consumer);

    boolean k(y yVar);

    y spliterator();

    Stream stream();
}
