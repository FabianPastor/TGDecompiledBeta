package a;

import java.util.function.ToIntFunction;

public final /* synthetic */ class H0 implements ToIntFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.ToIntFunction var_a;

    private /* synthetic */ H0(j$.util.function.ToIntFunction toIntFunction) {
        this.var_a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(j$.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof G0 ? ((G0) toIntFunction).var_a : new H0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.var_a.applyAsInt(obj);
    }
}
