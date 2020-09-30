package j$;

import j$.util.function.CLASSNAMEu;
import java.util.function.DoubleFunction;

/* renamed from: j$.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw implements CLASSNAMEu {
    final /* synthetic */ DoubleFunction a;

    private /* synthetic */ CLASSNAMEw(DoubleFunction doubleFunction) {
        this.a = doubleFunction;
    }

    public static /* synthetic */ CLASSNAMEu b(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return new CLASSNAMEw(doubleFunction);
    }

    public /* synthetic */ Object a(double d) {
        return this.a.apply(d);
    }
}
