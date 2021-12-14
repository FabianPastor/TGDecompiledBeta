package j$.wrappers;

import j$.util.function.A;
import java.util.function.ToDoubleFunction;

public final /* synthetic */ class C0 implements ToDoubleFunction {
    final /* synthetic */ A a;

    private /* synthetic */ C0(A a2) {
        this.a = a2;
    }

    public static /* synthetic */ ToDoubleFunction a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof B0 ? ((B0) a2).a : new C0(a2);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
