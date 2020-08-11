package j$;

import j$.util.function.E;
import java.util.function.IntToDoubleFunction;

public final /* synthetic */ class L implements E {
    final /* synthetic */ IntToDoubleFunction a;

    private /* synthetic */ L(IntToDoubleFunction intToDoubleFunction) {
        this.a = intToDoubleFunction;
    }

    public static /* synthetic */ E b(IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return new L(intToDoubleFunction);
    }

    public /* synthetic */ double a(int i) {
        return this.a.applyAsDouble(i);
    }
}
