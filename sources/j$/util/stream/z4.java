package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.w;
import j$.util.y;

final class z4 extends A4 implements w {
    z4(w wVar, long j, long j2) {
        super(wVar, j, j2);
    }

    z4(w wVar, long j, long j2, long j3, long j4) {
        super(wVar, j, j2, j3, j4, (CLASSNAMEp1) null);
    }

    /* access modifiers changed from: protected */
    public y a(y yVar, long j, long j2, long j3, long j4) {
        return new z4((w) yVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return y4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }
}
