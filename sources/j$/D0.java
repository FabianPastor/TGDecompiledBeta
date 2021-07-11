package j$;

import j$.util.function.ToIntFunction;

public final /* synthetic */ class D0 implements ToIntFunction {
    final /* synthetic */ java.util.function.ToIntFunction a;

    private /* synthetic */ D0(java.util.function.ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(java.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof E0 ? ((E0) toIntFunction).a : new D0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
