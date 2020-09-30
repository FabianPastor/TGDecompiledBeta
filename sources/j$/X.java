package j$;

import j$.util.function.N;
import java.util.function.LongToIntFunction;

public final /* synthetic */ class X implements N {
    final /* synthetic */ LongToIntFunction a;

    private /* synthetic */ X(LongToIntFunction longToIntFunction) {
        this.a = longToIntFunction;
    }

    public static /* synthetic */ N b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return new X(longToIntFunction);
    }

    public /* synthetic */ int a(long j) {
        return this.a.applyAsInt(j);
    }
}
