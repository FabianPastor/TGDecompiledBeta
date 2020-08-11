package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.J;

class S2 extends V2 {
    public /* bridge */ /* synthetic */ W2 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ W2 sequential() {
        super.sequential();
        return this;
    }

    S2(Spliterator spliterator, int sourceFlags, boolean parallel) {
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

    public void i(J action) {
        if (!isParallel()) {
            V2.O0(L0()).d(action);
        } else {
            super.i(action);
        }
    }

    public void b0(J action) {
        if (!isParallel()) {
            V2.O0(L0()).d(action);
        } else {
            super.b0(action);
        }
    }
}
