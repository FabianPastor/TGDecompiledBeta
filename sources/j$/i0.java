package j$;

import j$.util.function.ToLongFunction;

public final /* synthetic */ class i0 implements ToLongFunction {
    final /* synthetic */ java.util.function.ToLongFunction a;

    private /* synthetic */ i0(java.util.function.ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction b(java.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return new i0(toLongFunction);
    }

    public /* synthetic */ long a(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
