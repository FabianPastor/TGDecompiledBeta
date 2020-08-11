package j$.util;

import j$.util.function.Consumer;
import j$.util.function.J;
import java.util.Comparator;

final class e0 extends g0 implements U {
    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
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

    public /* bridge */ /* synthetic */ void d(J j) {
        super.forEachRemaining(j);
    }

    public /* bridge */ /* synthetic */ boolean i(J j) {
        super.tryAdvance(j);
        return false;
    }

    e0() {
    }
}
