package j$;

import java.util.function.ToDoubleFunction;

public final /* synthetic */ class F0 implements ToDoubleFunction {
    final /* synthetic */ j$.util.function.ToDoubleFunction a;

    private /* synthetic */ F0(j$.util.function.ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction a(j$.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof E0 ? ((E0) toDoubleFunction).a : new F0(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
