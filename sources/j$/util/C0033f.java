package j$.util;

import j$.D;
import j$.g0;
import j$.h0;
import j$.i0;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.io.Serializable;
import java.util.Comparator;

/* renamed from: j$.util.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements Comparator, Serializable {
    public final /* synthetic */ Comparator a;
    public final /* synthetic */ Function b;

    public /* synthetic */ CLASSNAMEf(Comparator comparator, Function function) {
        this.a = comparator;
        this.b = function;
    }

    public /* synthetic */ Comparator a(Function function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
    }

    public /* synthetic */ java.util.Comparator b(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, function, comparator);
    }

    public /* synthetic */ java.util.Comparator c(ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
    }

    public final int compare(Object obj, Object obj2) {
        return this.a.compare(this.b.apply(obj), this.b.apply(obj2));
    }

    public /* synthetic */ java.util.Comparator d(ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
    }

    public /* synthetic */ java.util.Comparator e(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
    }

    public /* synthetic */ java.util.Comparator reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function) {
        return a(D.c(function));
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function, java.util.Comparator comparator) {
        return b(D.c(function), comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparingDouble(java.util.function.ToDoubleFunction toDoubleFunction) {
        return c(g0.b(toDoubleFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(java.util.function.ToIntFunction toIntFunction) {
        return d(h0.b(toIntFunction));
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(java.util.function.ToLongFunction toLongFunction) {
        return e(i0.b(toLongFunction));
    }
}