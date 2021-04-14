package a;

import java.util.function.IntToDoubleFunction;

/* renamed from: a.a0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa0 {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntToDoubleFunction var_a;

    private /* synthetic */ CLASSNAMEa0(IntToDoubleFunction intToDoubleFunction) {
        this.var_a = intToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEa0 b(IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return intToDoubleFunction instanceof CLASSNAMEb0 ? ((CLASSNAMEb0) intToDoubleFunction).var_a : new CLASSNAMEa0(intToDoubleFunction);
    }

    public double a(int i) {
        return this.var_a.applyAsDouble(i);
    }
}
