package j$;

import j$.util.function.BiFunction;
import j$.util.function.Function;

/* renamed from: j$.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BiFunction {
    final /* synthetic */ java.util.function.BiFunction a;

    private /* synthetic */ CLASSNAMEv(java.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction b(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEw ? ((CLASSNAMEw) biFunction).a : new CLASSNAMEv(biFunction);
    }

    public /* synthetic */ BiFunction a(Function function) {
        return b(this.a.andThen(Q.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
