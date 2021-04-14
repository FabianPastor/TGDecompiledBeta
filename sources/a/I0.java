package a;

import j$.util.function.ToLongFunction;

public final /* synthetic */ class I0 implements ToLongFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.ToLongFunction var_a;

    private /* synthetic */ I0(java.util.function.ToLongFunction toLongFunction) {
        this.var_a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction a(java.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof J0 ? ((J0) toLongFunction).var_a : new I0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.var_a.applyAsLong(obj);
    }
}
