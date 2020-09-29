package j$;

import j$.util.function.w;
import java.util.function.DoubleToIntFunction;

public final /* synthetic */ class K implements w {
    final /* synthetic */ DoubleToIntFunction a;

    private /* synthetic */ K(DoubleToIntFunction doubleToIntFunction) {
        this.a = doubleToIntFunction;
    }

    public static /* synthetic */ w b(DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return new K(doubleToIntFunction);
    }

    public /* synthetic */ int a(double d) {
        return this.a.applyAsInt(d);
    }
}
