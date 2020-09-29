package j$;

import j$.util.function.CLASSNAMEu;
import java.util.function.DoubleFunction;

public final /* synthetic */ class H implements CLASSNAMEu {
    final /* synthetic */ DoubleFunction a;

    private /* synthetic */ H(DoubleFunction doubleFunction) {
        this.a = doubleFunction;
    }

    public static /* synthetic */ CLASSNAMEu b(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return new H(doubleFunction);
    }

    public /* synthetic */ Object a(double d) {
        return this.a.apply(d);
    }
}
