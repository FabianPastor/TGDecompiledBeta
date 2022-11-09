package j$.util.stream;

import j$.util.CLASSNAMEj;
/* renamed from: j$.util.stream.e0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEe0 extends AbstractCLASSNAMEi0 implements InterfaceCLASSNAMEj3 {
    @Override // j$.util.stream.AbstractCLASSNAMEi0, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        accept(Double.valueOf(d));
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return CLASSNAMEj.d(((Double) this.b).doubleValue());
        }
        return null;
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
