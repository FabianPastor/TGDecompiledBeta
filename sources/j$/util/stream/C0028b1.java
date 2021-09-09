package j$.util.stream;

import j$.util.function.q;
import j$.util.y;

/* renamed from: j$.util.stream.b1  reason: case insensitive filesystem */
class CLASSNAMEb1 extends CLASSNAMEe1 {
    CLASSNAMEb1(y yVar, int i, boolean z) {
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

    public void Z(q qVar) {
        if (!isParallel()) {
            CLASSNAMEe1.M0(J0()).d(qVar);
            return;
        }
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    public void d(q qVar) {
        if (!isParallel()) {
            CLASSNAMEe1.M0(J0()).d(qVar);
        } else {
            super.d(qVar);
        }
    }

    public /* bridge */ /* synthetic */ CLASSNAMEf1 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ CLASSNAMEf1 sequential() {
        sequential();
        return this;
    }
}
