package j$.wrappers;

import j$.util.function.h;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class I implements h {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ I(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ h a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof J ? ((J) doubleToLongFunction).a : new I(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
