package j$;

import j$.util.function.ToLongFunction;

public final /* synthetic */ class I0 implements ToLongFunction {
    final /* synthetic */ java.util.function.ToLongFunction a;

    private /* synthetic */ I0(java.util.function.ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction a(java.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof J0 ? ((J0) toLongFunction).a : new I0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
