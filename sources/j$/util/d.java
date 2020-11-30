package j$.util;

import j$.E0;
import j$.G0;
import j$.I0;
import j$.P;
import j$.util.Comparator;
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final /* synthetic */ class d implements Comparator, Serializable {
    public final /* synthetic */ Comparator a;
    public final /* synthetic */ Comparator b;

    public /* synthetic */ d(Comparator comparator, Comparator comparator2) {
        this.a = comparator;
        this.b = comparator2;
    }

    public final int compare(Object obj, Object obj2) {
        Comparator comparator = this.a;
        Comparator comparator2 = this.b;
        int compare = comparator.compare(obj, obj2);
        return compare != 0 ? compare : comparator2.compare(obj, obj2);
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
