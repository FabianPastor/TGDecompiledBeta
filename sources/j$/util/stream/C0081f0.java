package j$.util.stream;

import j$.util.CLASSNAMEk;
/* renamed from: j$.util.stream.f0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEf0 extends AbstractCLASSNAMEi0 implements InterfaceCLASSNAMEk3 {
    @Override // j$.util.stream.AbstractCLASSNAMEi0, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(int i) {
        accept(Integer.valueOf(i));
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return CLASSNAMEk.d(((Integer) this.b).intValue());
        }
        return null;
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
