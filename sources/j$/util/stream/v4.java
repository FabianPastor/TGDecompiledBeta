package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
import j$.util.y;

final class v4 extends A4 implements u {
    v4(u uVar, long j, long j2) {
        super(uVar, j, j2);
    }

    v4(u uVar, long j, long j2, long j3, long j4) {
        super(uVar, j, j2, j3, j4, (CLASSNAMEp1) null);
    }

    /* access modifiers changed from: protected */
    public y a(y yVar, long j, long j2, long j3, long j4) {
        return new v4((u) yVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return u4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }
}
