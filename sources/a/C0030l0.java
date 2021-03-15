package a;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: a.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 implements LongFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ D var_a;

    private /* synthetic */ CLASSNAMEl0(D d) {
        this.var_a = d;
    }

    public static /* synthetic */ LongFunction a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof CLASSNAMEk0 ? ((CLASSNAMEk0) d).var_a : new CLASSNAMEl0(d);
    }

    public /* synthetic */ Object apply(long j) {
        return this.var_a.apply(j);
    }
}
