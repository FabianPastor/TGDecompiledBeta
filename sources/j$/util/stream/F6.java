package j$.util.stream;

import j$.util.S;
import j$.util.function.B;
import j$.util.function.V;

final class F6 extends H6 implements S {
    public /* bridge */ /* synthetic */ void c(B b) {
        super.forEachRemaining(b);
    }

    public /* bridge */ /* synthetic */ boolean f(B b) {
        return super.tryAdvance(b);
    }

    public /* bridge */ /* synthetic */ S trySplit() {
        return (S) super.trySplit();
    }

    F6(V v) {
        super(v);
    }
}
