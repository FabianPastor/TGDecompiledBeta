package j$.util.stream;

import j$.util.function.q;
import j$.util.u;

/* renamed from: j$.util.stream.a1  reason: case insensitive filesystem */
class CLASSNAMEa1 extends CLASSNAMEd1 {
    CLASSNAMEa1(u uVar, int i, boolean z) {
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

    public void Z(q qVar) {
        if (!isParallel()) {
            CLASSNAMEd1.M0(J0()).d(qVar);
            return;
        }
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    public void d(q qVar) {
        if (!isParallel()) {
            CLASSNAMEd1.M0(J0()).d(qVar);
        } else {
            super.d(qVar);
        }
    }

    public /* bridge */ /* synthetic */ CLASSNAMEe1 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ CLASSNAMEe1 sequential() {
        sequential();
        return this;
    }
}
