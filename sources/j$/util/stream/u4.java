package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class u4 extends z4 implements j$.util.t {
    /* JADX INFO: Access modifiers changed from: package-private */
    public u4(j$.util.t tVar, long j, long j2) {
        super(tVar, j, j2);
    }

    u4(j$.util.t tVar, long j, long j2, long j3, long j4) {
        super(tVar, j, j2, j3, j4, null);
    }

    @Override // j$.util.stream.D4
    protected j$.util.u a(j$.util.u uVar, long j, long j2, long j3, long j4) {
        return new u4((j$.util.t) uVar, j, j2, j3, j4);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.j(this, consumer);
    }

    @Override // j$.util.stream.z4
    protected /* bridge */ /* synthetic */ Object f() {
        return t4.a;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.b(this, consumer);
    }
}
