package j$;

import j$.util.function.ToIntFunction;

public final /* synthetic */ class G0 implements ToIntFunction {
    final /* synthetic */ java.util.function.ToIntFunction a;

    private /* synthetic */ G0(java.util.function.ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(java.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof H0 ? ((H0) toIntFunction).a : new G0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
