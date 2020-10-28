package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.i5  reason: case insensitive filesystem */
class CLASSNAMEi5 extends CLASSNAMEl5 {
    CLASSNAMEi5(Spliterator spliterator, int i, boolean z) {
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

    public void f(Consumer consumer) {
        if (!isParallel()) {
            I0().forEachRemaining(consumer);
            return;
        }
        consumer.getClass();
        w0(new W1(consumer, true));
    }

    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            I0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }
}
