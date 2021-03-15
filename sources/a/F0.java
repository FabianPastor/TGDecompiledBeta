package a;

import java.util.function.ToDoubleFunction;

public final /* synthetic */ class F0 implements ToDoubleFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.ToDoubleFunction var_a;

    private /* synthetic */ F0(j$.util.function.ToDoubleFunction toDoubleFunction) {
        this.var_a = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction a(j$.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof E0 ? ((E0) toDoubleFunction).var_a : new F0(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.var_a.applyAsDouble(obj);
    }
}
