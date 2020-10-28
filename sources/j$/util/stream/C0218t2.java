package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.u;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
class CLASSNAMEt2 extends CLASSNAMEw2 {
    CLASSNAMEt2(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    public void E(u uVar) {
        if (!isParallel()) {
            CLASSNAMEw2.L0(I0()).c(uVar);
            return;
        }
        uVar.getClass();
        w0(new U1(uVar, true));
    }

    /* access modifiers changed from: package-private */
    public final boolean F0() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        throw new UnsupportedOperationException();
    }

    public void Q(u uVar) {
        if (!isParallel()) {
            CLASSNAMEw2.L0(I0()).c(uVar);
        } else {
            super.Q(uVar);
        }
    }

    public /* bridge */ /* synthetic */ CLASSNAMEx2 parallel() {
        parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ CLASSNAMEx2 sequential() {
        sequential();
        return this;
    }
}
