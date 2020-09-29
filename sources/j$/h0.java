package j$;

import j$.util.function.M;
import java.util.function.LongToDoubleFunction;

public final /* synthetic */ class h0 implements M {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ h0(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ M b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return new h0(longToDoubleFunction);
    }

    public /* synthetic */ double a(long j) {
        return this.a.applyAsDouble(j);
    }
}
