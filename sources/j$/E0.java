package j$;

import java.util.function.ToIntFunction;

public final /* synthetic */ class E0 implements ToIntFunction {
    final /* synthetic */ j$.util.function.ToIntFunction a;

    private /* synthetic */ E0(j$.util.function.ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(j$.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof D0 ? ((D0) toIntFunction).a : new E0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
