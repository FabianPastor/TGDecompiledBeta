package j$;

import j$.util.function.BiFunction;

public final /* synthetic */ class A implements BiFunction {
    final /* synthetic */ java.util.function.BiFunction a;

    private /* synthetic */ A(java.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction b(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return new A(biFunction);
    }

    public /* synthetic */ Object a(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
