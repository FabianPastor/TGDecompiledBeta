package j$;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class I implements t {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ I(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ t a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof J ? ((J) doubleToLongFunction).a : new I(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
