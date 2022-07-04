package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
import j$.util.v;

final class y4 extends z4 implements v {
    y4(v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    y4(v vVar, long j, long j2, long j3, long j4) {
        super(vVar, j, j2, j3, j4, (CLASSNAMEo1) null);
    }

    /* access modifiers changed from: protected */
    public u a(u uVar, long j, long j2, long j3, long j4) {
        return new y4((v) uVar, j, j2, j3, j4);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object f() {
        return x4.a;
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }
}
