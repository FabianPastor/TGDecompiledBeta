package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.Function;

/* renamed from: j$.wrappers.t  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt implements BiFunction {
    final /* synthetic */ j$.util.function.BiFunction a;

    private /* synthetic */ CLASSNAMEt(j$.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction a(j$.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof CLASSNAMEs ? ((CLASSNAMEs) biFunction).a : new CLASSNAMEt(biFunction);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return a(this.a.andThen(M.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
