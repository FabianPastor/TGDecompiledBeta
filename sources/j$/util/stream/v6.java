package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.Spliterator;
import j$.util.function.Consumer;

final class v6 extends x6 implements D {
    v6(D d, long j, long j2) {
        super(d, j, j2);
    }

    v6(D d, long j, long j2, long j3, long j4) {
        super(d, j, j2, j3, j4, (CLASSNAMEi6) null);
    }

    /* access modifiers changed from: protected */
    public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
        return new v6((D) spliterator, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return D0.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.b(this, consumer);
    }
}
