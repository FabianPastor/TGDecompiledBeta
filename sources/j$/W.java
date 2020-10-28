package j$;

import j$.util.function.v;
import java.util.function.IntFunction;

public final /* synthetic */ class W implements v {
    final /* synthetic */ IntFunction a;

    private /* synthetic */ W(IntFunction intFunction) {
        this.a = intFunction;
    }

    public static /* synthetic */ v a(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof X ? ((X) intFunction).a : new W(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}
