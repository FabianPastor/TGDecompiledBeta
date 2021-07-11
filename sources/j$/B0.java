package j$;

import j$.util.function.ToDoubleFunction;

public final /* synthetic */ class B0 implements ToDoubleFunction {
    final /* synthetic */ java.util.function.ToDoubleFunction a;

    private /* synthetic */ B0(java.util.function.ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction a(java.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof C0 ? ((C0) toDoubleFunction).a : new B0(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
