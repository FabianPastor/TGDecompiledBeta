package j$.util.stream;

import j$.util.function.Consumer;

final class Y1 extends Z1 {
    final Consumer b;

    Y1(Consumer consumer, boolean ordered) {
        super(ordered);
        this.b = consumer;
    }

    public void accept(Object t) {
        this.b.accept(t);
    }
}
