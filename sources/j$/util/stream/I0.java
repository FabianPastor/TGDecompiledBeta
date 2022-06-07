package j$.util.stream;

import j$.util.function.l;
import j$.util.u;

class I0 extends L0 {
    I0(u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    /* access modifiers changed from: package-private */
    public final boolean G0() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        throw new UnsupportedOperationException();
    }

    public void I(l lVar) {
        if (!isParallel()) {
            L0.M0(J0()).c(lVar);
            return;
        }
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, true));
    }

    public void U(l lVar) {
        if (!isParallel()) {
            L0.M0(J0()).c(lVar);
        } else {
            super.U(lVar);
        }
    }

    public /* bridge */ /* synthetic */ IntStream parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ IntStream sequential() {
        sequential();
        return this;
    }
}
