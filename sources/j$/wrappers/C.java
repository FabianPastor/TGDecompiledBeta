package j$.wrappers;

import j$.util.function.g;
import java.util.function.DoubleFunction;

public final /* synthetic */ class C implements g {
    final /* synthetic */ DoubleFunction a;

    private /* synthetic */ C(DoubleFunction doubleFunction) {
        this.a = doubleFunction;
    }

    public static /* synthetic */ g a(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof D ? ((D) doubleFunction).a : new C(doubleFunction);
    }

    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
