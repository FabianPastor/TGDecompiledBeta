package j$.wrappers;

import j$.util.function.C;
import java.util.function.ToLongFunction;

public final /* synthetic */ class F0 implements C {
    final /* synthetic */ ToLongFunction a;

    private /* synthetic */ F0(ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ C a(ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof G0 ? ((G0) toLongFunction).a : new F0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
