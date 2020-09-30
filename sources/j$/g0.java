package j$;

import j$.util.function.ToDoubleFunction;

public final /* synthetic */ class g0 implements ToDoubleFunction {
    final /* synthetic */ java.util.function.ToDoubleFunction a;

    private /* synthetic */ g0(java.util.function.ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction b(java.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return new g0(toDoubleFunction);
    }

    public /* synthetic */ double a(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
