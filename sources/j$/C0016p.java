package j$;

import j$.util.function.BiFunction;

/* renamed from: j$.p  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp implements BiFunction {
    final /* synthetic */ java.util.function.BiFunction a;

    private /* synthetic */ CLASSNAMEp(java.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction b(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return new CLASSNAMEp(biFunction);
    }

    public /* synthetic */ Object a(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
