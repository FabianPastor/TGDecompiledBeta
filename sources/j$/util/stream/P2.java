package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.y;

class P2 extends S2 {
    P2(Spliterator spliterator, int i, boolean z) {
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

    public void X(y yVar) {
        if (!isParallel()) {
            S2.L0(I0()).d(yVar);
            return;
        }
        yVar.getClass();
        w0(new V1(yVar, true));
    }

    public void e(y yVar) {
        if (!isParallel()) {
            S2.L0(I0()).d(yVar);
        } else {
            super.e(yVar);
        }
    }

    public /* bridge */ /* synthetic */ T2 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ T2 sequential() {
        sequential();
        return this;
    }
}
