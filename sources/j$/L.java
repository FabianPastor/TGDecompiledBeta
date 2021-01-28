package j$;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class L implements t {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ L(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ t a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof M ? ((M) doubleToLongFunction).a : new L(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
