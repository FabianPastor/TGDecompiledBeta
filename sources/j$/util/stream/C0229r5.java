package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.r5  reason: case insensitive filesystem */
class CLASSNAMEr5 extends CLASSNAMEu5 {
    CLASSNAMEr5(Spliterator spliterator, int sourceFlags, boolean parallel) {
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

    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            L0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }

    public void j(Consumer consumer) {
        if (!isParallel()) {
            L0().forEachRemaining(consumer);
        } else {
            super.j(consumer);
        }
    }
}
