package j$.wrappers;

import j$.util.function.A;
import java.util.function.ToLongFunction;

public final /* synthetic */ class G0 implements ToLongFunction {
    final /* synthetic */ A a;

    private /* synthetic */ G0(A a2) {
        this.a = a2;
    }

    public static /* synthetic */ ToLongFunction a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof F0 ? ((F0) a2).a : new G0(a2);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
