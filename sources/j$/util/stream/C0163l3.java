package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.l3  reason: case insensitive filesystem */
interface CLASSNAMEl3 {
    CLASSNAMEl3 b(int i);

    long count();

    void forEach(Consumer consumer);

    void j(Object[] objArr, int i);

    int o();

    Object[] q(v vVar);

    CLASSNAMEl3 r(long j, long j2, v vVar);

    Spliterator spliterator();
}
