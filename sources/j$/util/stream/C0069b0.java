package j$.util.stream;

import j$.util.function.J;
import j$.util.function.Predicate;

/* renamed from: j$.util.stream.b0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb0 implements J {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ N1 var_a;
    public final /* synthetic */ Predicate b;

    public /* synthetic */ CLASSNAMEb0(N1 n1, Predicate predicate) {
        this.var_a = n1;
        this.b = predicate;
    }

    public final Object get() {
        return new I1(this.var_a, this.b);
    }
}
