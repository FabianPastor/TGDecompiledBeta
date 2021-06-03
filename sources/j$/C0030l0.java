package j$;

import java.util.function.LongToDoubleFunction;

/* renamed from: j$.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ CLASSNAMEl0(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEl0 b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof CLASSNAMEm0 ? ((CLASSNAMEm0) longToDoubleFunction).a : new CLASSNAMEl0(longToDoubleFunction);
    }

    public double a(long j) {
        return this.a.applyAsDouble(j);
    }
}
