package j$;

import j$.util.function.ToLongFunction;

public final /* synthetic */ class t0 implements ToLongFunction {
    final /* synthetic */ java.util.function.ToLongFunction a;

    private /* synthetic */ t0(java.util.function.ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction b(java.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return new t0(toLongFunction);
    }

    public /* synthetic */ long a(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
