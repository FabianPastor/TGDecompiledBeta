package j$.util.stream;

import j$.util.function.E;
import j$.util.function.Predicate;

/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements E {
    public final /* synthetic */ Z2 a;
    public final /* synthetic */ Predicate b;

    public /* synthetic */ CLASSNAMEd0(Z2 z2, Predicate predicate) {
        this.a = z2;
        this.b = predicate;
    }

    public final Object get() {
        return new U2(this.a, this.b);
    }
}
