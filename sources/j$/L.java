package j$;

import j$.util.function.s;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class L implements s {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ L(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ s a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof M ? ((M) doubleToLongFunction).a : new L(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
