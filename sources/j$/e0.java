package j$;

import j$.util.function.K;
import java.util.function.LongFunction;

public final /* synthetic */ class e0 implements K {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ e0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ K b(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return new e0(longFunction);
    }

    public /* synthetic */ Object a(long j) {
        return this.a.apply(j);
    }
}
