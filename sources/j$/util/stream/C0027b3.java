package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;

/* renamed from: j$.util.stream.b3  reason: case insensitive filesystem */
class CLASSNAMEb3 extends CLASSNAMEe3 {
    CLASSNAMEb3(u uVar, int i, boolean z) {
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

    public void e(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
            return;
        }
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, true));
    }

    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }
}
