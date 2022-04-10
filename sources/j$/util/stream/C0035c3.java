package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.y;

/* renamed from: j$.util.stream.c3  reason: case insensitive filesystem */
class CLASSNAMEc3 extends CLASSNAMEf3 {
    CLASSNAMEc3(y yVar, int i, boolean z) {
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
