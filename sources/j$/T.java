package j$;

import j$.util.function.K;
import java.util.function.LongFunction;

public final /* synthetic */ class T implements K {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ T(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ K b(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return new T(longFunction);
    }

    public /* synthetic */ Object a(long j) {
        return this.a.apply(j);
    }
}
