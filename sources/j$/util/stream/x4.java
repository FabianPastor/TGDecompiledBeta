package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.v;
import j$.util.y;

final class x4 extends A4 implements v {
    x4(v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    x4(v vVar, long j, long j2, long j3, long j4) {
        super(vVar, j, j2, j3, j4, (CLASSNAMEp1) null);
    }

    /* access modifiers changed from: protected */
    public y a(y yVar, long j, long j2, long j3, long j4) {
        return new x4((v) yVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return w4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }
}
