package j$.util;

import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Collections;

public interface Comparator {
    int compare(Object obj, Object obj2);

    boolean equals(Object obj);

    java.util.Comparator reversed();

    java.util.Comparator thenComparing(Function function);

    java.util.Comparator thenComparing(Function function, java.util.Comparator comparator);

    java.util.Comparator thenComparing(java.util.Comparator comparator);

    java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction);

    java.util.Comparator thenComparingInt(ToIntFunction toIntFunction);

    java.util.Comparator thenComparingLong(ToLongFunction toLongFunction);

    /* renamed from: j$.util.Comparator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static java.util.Comparator $default$reversed(java.util.Comparator _this) {
            return Collections.reverseOrder(_this);
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, java.util.Comparator other) {
            other.getClass();
            return new CLASSNAMEd(_this, other);
        }

        public static /* synthetic */ int k(java.util.Comparator _this, java.util.Comparator other, Object c1, Object c2) {
            int res = _this.compare(c1, c2);
            return res != 0 ? res : other.compare(c1, c2);
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, Function function, java.util.Comparator keyComparator) {
            return CLASSNAMEl.a(_this, b(function, keyComparator));
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, Function function) {
            return CLASSNAMEl.a(_this, a(function));
        }

        public static java.util.Comparator $default$thenComparingInt(java.util.Comparator _this, ToIntFunction toIntFunction) {
            return CLASSNAMEl.a(_this, d(toIntFunction));
        }

        public static java.util.Comparator $default$thenComparingLong(java.util.Comparator _this, ToLongFunction toLongFunction) {
            return CLASSNAMEl.a(_this, e(toLongFunction));
        }

        public static java.util.Comparator $default$thenComparingDouble(java.util.Comparator _this, ToDoubleFunction toDoubleFunction) {
            return CLASSNAMEl.a(_this, c(toDoubleFunction));
        }

        public static java.util.Comparator m() {
            return Collections.reverseOrder();
        }

        public static java.util.Comparator l() {
            return CLASSNAMEm.INSTANCE;
        }

        public static java.util.Comparator b(Function function, java.util.Comparator keyComparator) {
            function.getClass();
            keyComparator.getClass();
            return new CLASSNAMEf(keyComparator, function);
        }

        public static java.util.Comparator a(Function function) {
            function.getClass();
            return new CLASSNAMEg(function);
        }

        public static java.util.Comparator d(ToIntFunction toIntFunction) {
            toIntFunction.getClass();
            return new CLASSNAMEe(toIntFunction);
        }

        public static java.util.Comparator e(ToLongFunction toLongFunction) {
            toLongFunction.getClass();
            return new CLASSNAMEc(toLongFunction);
        }

        public static /* synthetic */ int j(ToLongFunction keyExtractor, Object c1, Object c2) {
            return (keyExtractor.a(c1) > keyExtractor.a(c2) ? 1 : (keyExtractor.a(c1) == keyExtractor.a(c2) ? 0 : -1));
        }

        public static java.util.Comparator c(ToDoubleFunction toDoubleFunction) {
            toDoubleFunction.getClass();
            return new CLASSNAMEh(toDoubleFunction);
        }
    }
}
