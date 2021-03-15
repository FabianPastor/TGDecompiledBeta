package a;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class L implements t {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleToLongFunction var_a;

    private /* synthetic */ L(DoubleToLongFunction doubleToLongFunction) {
        this.var_a = doubleToLongFunction;
    }

    public static /* synthetic */ t a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof M ? ((M) doubleToLongFunction).var_a : new L(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.var_a.applyAsLong(d);
    }
}
