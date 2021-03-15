package a;

import j$.util.function.ToIntFunction;

public final /* synthetic */ class G0 implements ToIntFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.ToIntFunction var_a;

    private /* synthetic */ G0(java.util.function.ToIntFunction toIntFunction) {
        this.var_a = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction a(java.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof H0 ? ((H0) toIntFunction).var_a : new G0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.var_a.applyAsInt(obj);
    }
}
