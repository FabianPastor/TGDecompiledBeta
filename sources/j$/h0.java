package j$;

import j$.util.function.ToIntFunction;

public final /* synthetic */ class h0 implements ToIntFunction {
    final /* synthetic */ java.util.function.ToIntFunction a;

    private /* synthetic */ h0(java.util.function.ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction b(java.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return new h0(toIntFunction);
    }

    public /* synthetic */ int a(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
