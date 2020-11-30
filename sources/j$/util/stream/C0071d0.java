package j$.util.stream;

import j$.util.function.J;
import j$.util.function.Predicate;

/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements J {
    public final /* synthetic */ N1 a;
    public final /* synthetic */ Predicate b;

    public /* synthetic */ CLASSNAMEd0(N1 n1, Predicate predicate) {
        this.a = n1;
        this.b = predicate;
    }

    public final Object get() {
        return new I1(this.a, this.b);
    }
}
