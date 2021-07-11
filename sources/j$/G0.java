package j$;

import java.util.function.ToLongFunction;

public final /* synthetic */ class G0 implements ToLongFunction {
    final /* synthetic */ j$.util.function.ToLongFunction a;

    private /* synthetic */ G0(j$.util.function.ToLongFunction toLongFunction) {
        this.a = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction a(j$.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof F0 ? ((F0) toLongFunction).a : new G0(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
