package j$;

import java.util.function.BiFunction;
import java.util.function.Function;

/* renamed from: j$.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw implements BiFunction {
    final /* synthetic */ j$.util.function.BiFunction a;

    private /* synthetic */ CLASSNAMEw(j$.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction a(j$.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEv ? ((CLASSNAMEv) biFunction).a : new CLASSNAMEw(biFunction);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return a(this.a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
