package j$;

import java.util.function.ToLongFunction;

public final /* synthetic */ class J0 implements ToLongFunction {
    final /* synthetic */ j$.util.function.ToLongFunction a;

    private /* synthetic */ J0(j$.util.function.ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction a(j$.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof I0 ? ((I0) toLongFunction).a : new J0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
