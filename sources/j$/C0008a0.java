package j$;

import java.util.function.IntToDoubleFunction;

/* renamed from: j$.a0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa0 {
    final /* synthetic */ IntToDoubleFunction a;

    private /* synthetic */ CLASSNAMEa0(IntToDoubleFunction intToDoubleFunction) {
        this.a = intToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEa0 b(IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return intToDoubleFunction instanceof CLASSNAMEb0 ? ((CLASSNAMEb0) intToDoubleFunction).a : new CLASSNAMEa0(intToDoubleFunction);
    }

    public double a(int i) {
        return this.a.applyAsDouble(i);
    }
}
