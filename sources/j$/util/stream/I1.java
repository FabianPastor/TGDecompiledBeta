package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.CLASSNAMEt;

class I1 extends L1 {
    public /* bridge */ /* synthetic */ M1 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ M1 sequential() {
        super.sequential();
        return this;
    }

    I1(Spliterator spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    /* access modifiers changed from: package-private */
    public final boolean I0() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public final G5 J0(int flags, G5 g5) {
        throw new UnsupportedOperationException();
    }

    public void n(CLASSNAMEt consumer) {
        if (!isParallel()) {
            L1.O0(L0()).e(consumer);
        } else {
            super.n(consumer);
        }
    }

    public void m0(CLASSNAMEt consumer) {
        if (!isParallel()) {
            L1.O0(L0()).e(consumer);
        } else {
            super.m0(consumer);
        }
    }
}
