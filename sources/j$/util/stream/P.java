package j$.util.stream;

import j$.util.function.f;
import j$.util.u;

class P extends T {
    P(u uVar, int i, boolean z) {
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

    public void j(f fVar) {
        if (!isParallel()) {
            T.M0(J0()).e(fVar);
        } else {
            super.j(fVar);
        }
    }

    public void l0(f fVar) {
        if (!isParallel()) {
            T.M0(J0()).e(fVar);
            return;
        }
        fVar.getClass();
        x0(new CLASSNAMEk0(fVar, true));
    }

    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
