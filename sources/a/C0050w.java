package a;

import java.util.function.BiFunction;
import java.util.function.Function;

/* renamed from: a.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw implements BiFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.BiFunction var_a;

    private /* synthetic */ CLASSNAMEw(j$.util.function.BiFunction biFunction) {
        this.var_a = biFunction;
    }

    public static /* synthetic */ BiFunction a(j$.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEv ? ((CLASSNAMEv) biFunction).var_a : new CLASSNAMEw(biFunction);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return a(this.var_a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.var_a.apply(obj, obj2);
    }
}
