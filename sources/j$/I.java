package j$;

import j$.util.function.C;
import java.util.function.IntFunction;

public final /* synthetic */ class I implements C {
    final /* synthetic */ IntFunction a;

    private /* synthetic */ I(IntFunction intFunction) {
        this.a = intFunction;
    }

    public static /* synthetic */ C b(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return new I(intFunction);
    }

    public /* synthetic */ Object a(int i) {
        return this.a.apply(i);
    }
}