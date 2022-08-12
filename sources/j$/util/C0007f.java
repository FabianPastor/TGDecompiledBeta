package j$.util;

import j$.util.function.A;
import j$.util.function.z;
import j$.wrappers.B0;
import j$.wrappers.D0;
import j$.wrappers.F0;
import j$.wrappers.M;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* renamed from: j$.util.f  reason: case insensitive filesystem */
enum CLASSNAMEf implements Comparator, CLASSNAMEe {
    INSTANCE;

    public int compare(Object obj, Object obj2) {
        return ((Comparable) obj).compareTo((Comparable) obj2);
    }

    public Comparator reversed() {
        return Comparator$CC.reverseOrder();
    }

    public Comparator thenComparing(Comparator comparator) {
        comparator.getClass();
        return new CLASSNAMEc((Comparator) this, comparator);
    }

    public Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        z a2 = B0.a(toDoubleFunction);
        a2.getClass();
        return CLASSNAMEa.H(this, new CLASSNAMEd(a2));
    }

    public Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return CLASSNAMEa.H(this, Comparator$CC.comparingInt(D0.a(toIntFunction)));
    }

    public Comparator thenComparingLong(ToLongFunction toLongFunction) {
        A a2 = F0.a(toLongFunction);
        a2.getClass();
        return CLASSNAMEa.H(this, new CLASSNAMEd(a2));
    }

    public Comparator thenComparing(Function function) {
        return CLASSNAMEa.H(this, Comparator$CC.comparing(M.a(function)));
    }

    public Comparator thenComparing(Function function, Comparator comparator) {
        j$.util.function.Function a2 = M.a(function);
        a2.getClass();
        comparator.getClass();
        return CLASSNAMEa.H(this, new CLASSNAMEc(comparator, a2));
    }
}
