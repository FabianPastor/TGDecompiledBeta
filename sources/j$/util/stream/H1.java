package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.q;

class H1 extends K1 {
    H1(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    /* access modifiers changed from: package-private */
    public final boolean F0() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        throw new UnsupportedOperationException();
    }

    public void k(q qVar) {
        if (!isParallel()) {
            K1.L0(I0()).e(qVar);
        } else {
            super.k(qVar);
        }
    }

    public void k0(q qVar) {
        if (!isParallel()) {
            K1.L0(I0()).e(qVar);
            return;
        }
        qVar.getClass();
        w0(new T1(qVar, true));
    }

    public /* bridge */ /* synthetic */ L1 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ L1 sequential() {
        sequential();
        return this;
    }
}
