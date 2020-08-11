package j$;

import j$.util.function.M;
import java.util.function.LongToDoubleFunction;

public final /* synthetic */ class W implements M {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ W(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ M b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return new W(longToDoubleFunction);
    }

    public /* synthetic */ double a(long j) {
        return this.a.applyAsDouble(j);
    }
}
