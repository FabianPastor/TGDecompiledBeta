package a;

import java.util.function.ToLongFunction;

public final /* synthetic */ class J0 implements ToLongFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.ToLongFunction var_a;

    private /* synthetic */ J0(j$.util.function.ToLongFunction toLongFunction) {
        this.var_a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction a(j$.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof I0 ? ((I0) toLongFunction).var_a : new J0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.var_a.applyAsLong(obj);
    }
}
