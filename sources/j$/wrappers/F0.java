package j$.wrappers;

import j$.util.function.A;
import java.util.function.ToLongFunction;

public final /* synthetic */ class F0 implements A {
    final /* synthetic */ ToLongFunction a;

    private /* synthetic */ F0(ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ A a(ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof G0 ? ((G0) toLongFunction).a : new F0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
