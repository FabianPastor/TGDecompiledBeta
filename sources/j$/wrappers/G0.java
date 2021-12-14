package j$.wrappers;

import j$.util.function.C;
import java.util.function.ToLongFunction;

public final /* synthetic */ class G0 implements ToLongFunction {
    final /* synthetic */ C a;

    private /* synthetic */ G0(C c) {
        this.a = c;
    }

    public static /* synthetic */ ToLongFunction a(C c) {
        if (c == null) {
            return null;
        }
        return c instanceof F0 ? ((F0) c).a : new G0(c);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
