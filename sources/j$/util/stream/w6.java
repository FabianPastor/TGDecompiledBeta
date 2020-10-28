package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.E;
import j$.util.Spliterator;
import j$.util.function.Consumer;

final class w6 extends x6 implements E {
    w6(E e, long j, long j2) {
        super(e, j, j2);
    }

    w6(E e, long j, long j2, long j3, long j4) {
        super(e, j, j2, j3, j4, (CLASSNAMEi6) null);
    }

    /* access modifiers changed from: protected */
    public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
        return new w6((E) spliterator, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return E0.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
    }
}
