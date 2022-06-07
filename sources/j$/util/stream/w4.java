package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;

final class w4 extends z4 implements u.a {
    w4(u.a aVar, long j, long j2) {
        super(aVar, j, j2);
    }

    w4(u.a aVar, long j, long j2, long j3, long j4) {
        super(aVar, j, j2, j3, j4, (CLASSNAMEo1) null);
    }

    /* access modifiers changed from: protected */
    public u a(u uVar, long j, long j2, long j3, long j4) {
        return new w4((u.a) uVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return v4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }
}
