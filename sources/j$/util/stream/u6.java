package j$.util.stream;

import j$.util.C;
import j$.util.CLASSNAMEw;
import j$.util.Spliterator;
import j$.util.function.Consumer;

final class u6 extends x6 implements C {
    u6(C c, long j, long j2) {
        super(c, j, j2);
    }

    u6(C c, long j, long j2, long j3, long j4) {
        super(c, j, j2, j3, j4, (CLASSNAMEi6) null);
    }

    /* access modifiers changed from: protected */
    public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
        return new u6((C) spliterator, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return C0.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }
}
