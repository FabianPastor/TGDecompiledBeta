package j$.util;

import a.E0;
import a.G0;
import a.I0;
import a.P;
import j$.util.Comparator;
import j$.util.function.ToDoubleFunction;
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final /* synthetic */ class c implements Comparator, Serializable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ ToDoubleFunction var_a;

    public /* synthetic */ c(ToDoubleFunction toDoubleFunction) {
        this.var_a = toDoubleFunction;
    }

    public final int compare(Object obj, Object obj2) {
        ToDoubleFunction toDoubleFunction = this.var_a;
        return Double.compare(toDoubleFunction.applyAsDouble(obj), toDoubleFunction.applyAsDouble(obj2));
    }

    public /* synthetic */ Comparator reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, P.c(function));
    }

    public /* synthetic */ java.util.Comparator thenComparingDouble(java.util.function.ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, E0.a(toDoubleFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, G0.a(toIntFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, I0.a(toLongFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, P.c(function), comparator);
    }
}
