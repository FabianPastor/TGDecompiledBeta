package j$.util.stream;

import j$.util.function.k;
import j$.util.y;

class I0 extends L0 {
    I0(y yVar, int i, boolean z) {
        super(yVar, i, z);
    }

    /* access modifiers changed from: package-private */
    public final boolean G0() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        throw new UnsupportedOperationException();
    }

    public void I(k kVar) {
        if (!isParallel()) {
            L0.M0(J0()).c(kVar);
            return;
        }
        kVar.getClass();
        x0(new CLASSNAMEl0(kVar, true));
    }

    public void U(k kVar) {
        if (!isParallel()) {
            L0.M0(J0()).c(kVar);
        } else {
            super.U(kVar);
        }
    }

    public /* bridge */ /* synthetic */ M0 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ M0 sequential() {
        sequential();
        return this;
    }
}
