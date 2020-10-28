package j$;

import j$.util.function.r;
import java.util.function.DoubleFunction;

public final /* synthetic */ class F implements r {
    final /* synthetic */ DoubleFunction a;

    private /* synthetic */ F(DoubleFunction doubleFunction) {
        this.a = doubleFunction;
    }

    public static /* synthetic */ r a(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof G ? ((G) doubleFunction).a : new F(doubleFunction);
    }

    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
