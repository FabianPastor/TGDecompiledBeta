package a;

import java.util.function.LongToDoubleFunction;

/* renamed from: a.o0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo0 {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongToDoubleFunction var_a;

    private /* synthetic */ CLASSNAMEo0(LongToDoubleFunction longToDoubleFunction) {
        this.var_a = longToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEo0 b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) longToDoubleFunction).var_a : new CLASSNAMEo0(longToDoubleFunction);
    }

    public double a(long j) {
        return this.var_a.applyAsDouble(j);
    }
}
