package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;

final class W1 extends Z1 implements D5 {
    final B b;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ B q(B b2) {
        return A.a(this, b2);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    W1(B consumer, boolean ordered) {
        super(ordered);
        this.b = consumer;
    }

    public void accept(int t) {
        this.b.accept(t);
    }
}
