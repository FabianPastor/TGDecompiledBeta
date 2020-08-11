package j$.util;

import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import java.util.Comparator;

final class c0 extends g0 implements P {
    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
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

    public /* bridge */ /* synthetic */ void e(CLASSNAMEt tVar) {
        super.forEachRemaining(tVar);
    }

    public /* bridge */ /* synthetic */ boolean j(CLASSNAMEt tVar) {
        super.tryAdvance(tVar);
        return false;
    }

    c0() {
    }
}
