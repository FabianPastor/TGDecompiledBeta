package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.Function;

/* renamed from: j$.wrappers.s  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs implements BiFunction {
    final /* synthetic */ java.util.function.BiFunction a;

    private /* synthetic */ CLASSNAMEs(java.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction a(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEt ? ((CLASSNAMEt) biFunction).a : new CLASSNAMEs(biFunction);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return a(this.a.andThen(N.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
