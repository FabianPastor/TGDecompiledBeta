package j$.util.stream;

import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;

final class W3 extends X3 implements U {
    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void d(J j) {
        super.forEachRemaining(j);
    }

    public /* bridge */ /* synthetic */ boolean i(J j) {
        return super.tryAdvance(j);
    }

    public /* bridge */ /* synthetic */ U trySplit() {
        return (U) super.trySplit();
    }

    W3(CLASSNAMEq3 cur) {
        super(cur);
    }
}
