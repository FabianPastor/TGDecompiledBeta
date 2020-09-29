package j$;

import j$.util.function.x;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class L implements x {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ L(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ x b(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return new L(doubleToLongFunction);
    }

    public /* synthetic */ long a(double d) {
        return this.a.applyAsLong(d);
    }
}
