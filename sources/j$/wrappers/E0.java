package j$.wrappers;

import j$.util.function.A;
import java.util.function.ToIntFunction;

public final /* synthetic */ class E0 implements ToIntFunction {
    final /* synthetic */ A a;

    private /* synthetic */ E0(A a2) {
        this.a = a2;
    }

    public static /* synthetic */ ToIntFunction a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof D0 ? ((D0) a2).a : new E0(a2);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
