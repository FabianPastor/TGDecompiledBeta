package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;

final class f0 extends g0 implements Spliterator {
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

    public /* bridge */ /* synthetic */ boolean a(Consumer consumer) {
        super.tryAdvance(consumer);
        return false;
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(Consumer consumer) {
        super.forEachRemaining(consumer);
    }

    f0() {
    }
}
