package a;

import j$.util.function.BiFunction;
import j$.util.function.Function;

/* renamed from: a.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BiFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.BiFunction var_a;

    private /* synthetic */ CLASSNAMEv(java.util.function.BiFunction biFunction) {
        this.var_a = biFunction;
    }

    public static /* synthetic */ BiFunction b(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEw ? ((CLASSNAMEw) biFunction).var_a : new CLASSNAMEv(biFunction);
    }

    public /* synthetic */ BiFunction a(Function function) {
        return b(this.var_a.andThen(Q.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.var_a.apply(obj, obj2);
    }
}
