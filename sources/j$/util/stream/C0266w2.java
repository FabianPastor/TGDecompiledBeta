package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.B;

/* renamed from: j$.util.stream.w2  reason: case insensitive filesystem */
class CLASSNAMEw2 extends CLASSNAMEz2 {
    public /* bridge */ /* synthetic */ A2 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ A2 sequential() {
        super.sequential();
        return this;
    }

    CLASSNAMEw2(Spliterator spliterator, int sourceFlags, boolean parallel) {
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

    public void S(B action) {
        if (!isParallel()) {
            CLASSNAMEz2.O0(L0()).c(action);
        } else {
            super.S(action);
        }
    }

    public void F(B action) {
        if (!isParallel()) {
            CLASSNAMEz2.O0(L0()).c(action);
        } else {
            super.F(action);
        }
    }
}
