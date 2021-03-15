package a;

import j$.util.function.r;
import java.util.function.DoubleFunction;

public final /* synthetic */ class F implements r {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleFunction var_a;

    private /* synthetic */ F(DoubleFunction doubleFunction) {
        this.var_a = doubleFunction;
    }

    public static /* synthetic */ r a(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof G ? ((G) doubleFunction).var_a : new F(doubleFunction);
    }

    public /* synthetic */ Object apply(double d) {
        return this.var_a.apply(d);
    }
}
