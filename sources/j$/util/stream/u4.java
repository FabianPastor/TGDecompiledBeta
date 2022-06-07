package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.t;
import j$.util.u;

final class u4 extends z4 implements t {
    u4(t tVar, long j, long j2) {
        super(tVar, j, j2);
    }

    u4(t tVar, long j, long j2, long j3, long j4) {
        super(tVar, j, j2, j3, j4, (CLASSNAMEo1) null);
    }

    /* access modifiers changed from: protected */
    public u a(u uVar, long j, long j2, long j3, long j4) {
        return new u4((t) uVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return t4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }
}
