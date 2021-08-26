package j$.wrappers;

import j$.util.function.B;
import java.util.function.ToLongFunction;

public final /* synthetic */ class G0 implements ToLongFunction {
    final /* synthetic */ B a;

    private /* synthetic */ G0(B b) {
        this.a = b;
    }

    public static /* synthetic */ ToLongFunction a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof F0 ? ((F0) b).a : new G0(b);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
