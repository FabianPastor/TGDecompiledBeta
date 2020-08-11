package j$;

import j$.util.function.w;
import java.util.function.DoubleToIntFunction;

/* renamed from: j$.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements w {
    final /* synthetic */ DoubleToIntFunction a;

    private /* synthetic */ CLASSNAMEz(DoubleToIntFunction doubleToIntFunction) {
        this.a = doubleToIntFunction;
    }

    public static /* synthetic */ w b(DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return new CLASSNAMEz(doubleToIntFunction);
    }

    public /* synthetic */ int a(double d) {
        return this.a.applyAsInt(d);
    }
}
