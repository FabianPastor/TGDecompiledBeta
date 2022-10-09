package j$.util.stream;

import j$.util.CLASSNAMEl;
/* renamed from: j$.util.stream.g0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEg0 extends AbstractCLASSNAMEi0 implements InterfaceCLASSNAMEl3 {
    @Override // j$.util.stream.AbstractCLASSNAMEi0, j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        accept(Long.valueOf(j));
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return CLASSNAMEl.d(((Long) this.b).longValue());
        }
        return null;
    }
}
