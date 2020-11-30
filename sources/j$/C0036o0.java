package j$;

import java.util.function.LongToDoubleFunction;

/* renamed from: j$.o0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo0 {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ CLASSNAMEo0(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEo0 b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) longToDoubleFunction).a : new CLASSNAMEo0(longToDoubleFunction);
    }

    public double a(long j) {
        return this.a.applyAsDouble(j);
    }
}
