package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.n0  reason: case insensitive filesystem */
final class CLASSNAMEn0 extends CLASSNAMEo0 {
    final Consumer b;

    CLASSNAMEn0(Consumer consumer, boolean z) {
        super(z);
        this.b = consumer;
    }

    public void accept(Object obj) {
        this.b.accept(obj);
    }
}
