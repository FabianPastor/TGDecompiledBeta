package j$.wrappers;

import j$.util.function.A;
import java.util.function.ToDoubleFunction;

public final /* synthetic */ class B0 implements A {
    final /* synthetic */ ToDoubleFunction a;

    private /* synthetic */ B0(ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public static /* synthetic */ A a(ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof C0 ? ((C0) toDoubleFunction).a : new B0(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
