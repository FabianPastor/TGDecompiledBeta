package j$.util;

import a.CLASSNAMEb;
import a.E0;
import a.G0;
import a.I0;
import a.P;
import j$.util.Comparator;
import j$.util.function.ToIntFunction;
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public final /* synthetic */ class e implements Comparator, Serializable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ ToIntFunction var_a;

    public /* synthetic */ e(ToIntFunction toIntFunction) {
        this.var_a = toIntFunction;
    }

    public final int compare(Object obj, Object obj2) {
        return CLASSNAMEb.a(this.var_a.applyAsInt(obj), this.var_a.applyAsInt(obj2));
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

    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, E0.a(toDoubleFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(java.util.function.ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, G0.a(toIntFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, I0.a(toLongFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, P.c(function), comparator);
    }
}
