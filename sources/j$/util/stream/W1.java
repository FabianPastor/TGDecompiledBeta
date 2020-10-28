package j$.util.stream;

import j$.util.function.Consumer;

final class W1 extends X1 {
    final Consumer b;

    W1(Consumer consumer, boolean z) {
        super(z);
        this.b = consumer;
    }

    public void accept(Object obj) {
        this.b.accept(obj);
    }
}
