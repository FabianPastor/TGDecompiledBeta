package j$.util.stream;

import j$.util.Q;
import j$.util.S;
import j$.util.function.B;
import j$.util.function.Consumer;

final class V3 extends X3 implements S {
    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void c(B b) {
        super.forEachRemaining(b);
    }

    public /* bridge */ /* synthetic */ boolean f(B b) {
        return super.tryAdvance(b);
    }

    public /* bridge */ /* synthetic */ S trySplit() {
        return (S) super.trySplit();
    }

    V3(CLASSNAMEo3 cur) {
        super(cur);
    }
}
