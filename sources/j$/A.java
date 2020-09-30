package j$;

import j$.util.function.x;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class A implements x {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ A(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ x b(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return new A(doubleToLongFunction);
    }

    public /* synthetic */ long a(double d) {
        return this.a.applyAsLong(d);
    }
}
