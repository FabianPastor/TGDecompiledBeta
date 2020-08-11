package j$.util;

import j$.util.function.B;
import j$.util.function.Consumer;
import java.util.Comparator;

final class d0 extends g0 implements S {
    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public /* bridge */ /* synthetic */ void c(B b) {
        super.forEachRemaining(b);
    }

    public /* bridge */ /* synthetic */ boolean f(B b) {
        super.tryAdvance(b);
        return false;
    }

    d0() {
    }
}
