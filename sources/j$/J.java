package j$;

import java.util.function.DoubleToIntFunction;

public final /* synthetic */ class J {
    final /* synthetic */ DoubleToIntFunction a;

    private /* synthetic */ J(DoubleToIntFunction doubleToIntFunction) {
        this.a = doubleToIntFunction;
    }

    public static /* synthetic */ J b(DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return doubleToIntFunction instanceof K ? ((K) doubleToIntFunction).a : new J(doubleToIntFunction);
    }

    public int a(double d) {
        return this.a.applyAsInt(d);
    }
}
