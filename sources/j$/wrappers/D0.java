package j$.wrappers;

import j$.util.function.B;
import java.util.function.ToIntFunction;

public final /* synthetic */ class D0 implements B {
    final /* synthetic */ ToIntFunction a;

    private /* synthetic */ D0(ToIntFunction toIntFunction) {
        this.a = toIntFunction;
    }

    public static /* synthetic */ B a(ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof E0 ? ((E0) toIntFunction).a : new D0(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
