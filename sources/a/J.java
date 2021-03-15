package a;

import java.util.function.DoubleToIntFunction;

public final /* synthetic */ class J {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleToIntFunction var_a;

    private /* synthetic */ J(DoubleToIntFunction doubleToIntFunction) {
        this.var_a = doubleToIntFunction;
    }

    public static /* synthetic */ J b(DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return doubleToIntFunction instanceof K ? ((K) doubleToIntFunction).var_a : new J(doubleToIntFunction);
    }

    public int a(double d) {
        return this.var_a.applyAsInt(d);
    }
}
