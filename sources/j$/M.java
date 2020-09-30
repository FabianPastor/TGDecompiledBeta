package j$;

import j$.util.function.F;
import java.util.function.IntToLongFunction;

public final /* synthetic */ class M implements F {
    final /* synthetic */ IntToLongFunction a;

    private /* synthetic */ M(IntToLongFunction intToLongFunction) {
        this.a = intToLongFunction;
    }

    public static /* synthetic */ F b(IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return new M(intToLongFunction);
    }

    public /* synthetic */ long a(int i) {
        return this.a.applyAsLong(i);
    }
}
