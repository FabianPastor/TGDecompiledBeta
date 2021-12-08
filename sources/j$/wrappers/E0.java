package j$.wrappers;

import j$.util.function.B;
import java.util.function.ToIntFunction;

public final /* synthetic */ class E0 implements ToIntFunction {
    final /* synthetic */ B a;

    private /* synthetic */ E0(B b) {
        this.a = b;
    }

    public static /* synthetic */ ToIntFunction a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof D0 ? ((D0) b).a : new E0(b);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.a.applyAsInt(obj);
    }
}
