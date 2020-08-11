package j$.util.stream;

import j$.util.function.I;
import j$.util.function.J;

final class X1 extends Z1 implements F5 {
    final J b;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    X1(J consumer, boolean ordered) {
        super(ordered);
        this.b = consumer;
    }

    public void accept(long t) {
        this.b.accept(t);
    }
}
