package j$;

import java.util.function.ToIntFunction;

public final /* synthetic */ class H0 implements ToIntFunction {
    final /* synthetic */ j$.util.function.ToIntFunction a;

    private /* synthetic */ H0(j$.util.function.ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(j$.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof G0 ? ((G0) toIntFunction).a : new H0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
