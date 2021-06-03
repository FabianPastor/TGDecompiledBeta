package j$.util;

import j$.B0;
import j$.D0;
import j$.F0;
import j$.M;
import j$.util.Comparator;
import j$.util.function.ToDoubleFunction;
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final /* synthetic */ class c implements Comparator, Serializable {
    public final /* synthetic */ ToDoubleFunction a;

    public /* synthetic */ c(ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public final int compare(Object obj, Object obj2) {
        ToDoubleFunction toDoubleFunction = this.a;
        return Double.compare(toDoubleFunction.applyAsDouble(obj), toDoubleFunction.applyAsDouble(obj2));
    }

    public /* synthetic */ Comparator reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, M.c(function));
    }

    public /* synthetic */ java.util.Comparator thenComparingDouble(java.util.function.ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, B0.a(toDoubleFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, D0.a(toIntFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, F0.a(toLongFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, M.c(function), comparator);
    }
}
