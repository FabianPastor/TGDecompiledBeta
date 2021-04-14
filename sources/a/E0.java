package a;

import j$.util.function.ToDoubleFunction;

public final /* synthetic */ class E0 implements ToDoubleFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.ToDoubleFunction var_a;

    private /* synthetic */ E0(java.util.function.ToDoubleFunction toDoubleFunction) {
        this.var_a = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction a(java.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof F0 ? ((F0) toDoubleFunction).var_a : new E0(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.var_a.applyAsDouble(obj);
    }
}
