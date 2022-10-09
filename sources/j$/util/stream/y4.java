package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class y4 extends z4 implements j$.util.v {
    /* JADX INFO: Access modifiers changed from: package-private */
    public y4(j$.util.v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    y4(j$.util.v vVar, long j, long j2, long j3, long j4) {
        super(vVar, j, j2, j3, j4, null);
    }

    @Override // j$.util.stream.D4
    protected j$.util.u a(j$.util.u uVar, long j, long j2, long j3, long j4) {
        return new y4((j$.util.v) uVar, j, j2, j3, j4);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.stream.z4
    protected /* bridge */ /* synthetic */ Object f() {
        return x4.a;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }
}
