package j$.util;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.wrappers.C$r8$wrapper$java$util$function$Function$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToIntFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToLongFunction$VWRP;
import java.io.Serializable;
import java.util.Comparator;

class Comparators {
    private Comparators() {
        throw new AssertionError("no instances");
    }

    enum NaturalOrderComparator implements Comparator<Comparable<Object>>, Comparator<Comparable<Object>> {
        INSTANCE;

        public int compare(Comparable<Object> c1, Comparable<Object> c2) {
            return c1.compareTo(c2);
        }

        public Comparator<Comparable<Object>> reversed() {
            return Comparator.CC.reverseOrder();
        }
    }

    static final class NullComparator<T> implements java.util.Comparator<T>, Serializable, Comparator<T> {
        private static final long serialVersionUID = -7569533591570686392L;
        private final boolean nullFirst;
        private final java.util.Comparator<T> real;

        public /* synthetic */ java.util.Comparator thenComparing(Function function) {
            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
        }

        public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing(this, function, comparator);
        }

        public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
        }

        public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function, java.util.Comparator comparator) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function), comparator);
        }

        public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
        }

        public /* synthetic */ java.util.Comparator thenComparingDouble(java.util.function.ToDoubleFunction toDoubleFunction) {
            return thenComparingDouble(C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP.convert(toDoubleFunction));
        }

        public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
        }

        public /* synthetic */ java.util.Comparator thenComparingInt(java.util.function.ToIntFunction toIntFunction) {
            return thenComparingInt(C$r8$wrapper$java$util$function$ToIntFunction$VWRP.convert(toIntFunction));
        }

        public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
        }

        public /* synthetic */ java.util.Comparator thenComparingLong(java.util.function.ToLongFunction toLongFunction) {
            return thenComparingLong(C$r8$wrapper$java$util$function$ToLongFunction$VWRP.convert(toLongFunction));
        }

        NullComparator(boolean nullFirst2, java.util.Comparator<? super T> real2) {
            this.nullFirst = nullFirst2;
            this.real = real2;
        }

        public int compare(T a, T b) {
            if (a == null) {
                if (b == null) {
                    return 0;
                }
                if (this.nullFirst) {
                    return -1;
                }
                return 1;
            } else if (b != null) {
                java.util.Comparator<T> comparator = this.real;
                if (comparator == null) {
                    return 0;
                }
                return comparator.compare(a, b);
            } else if (this.nullFirst) {
                return 1;
            } else {
                return -1;
            }
        }

        public java.util.Comparator<T> thenComparing(java.util.Comparator<? super T> other) {
            other.getClass();
            boolean z = this.nullFirst;
            java.util.Comparator<T> comparator = this.real;
            return new NullComparator(z, comparator == null ? other : Comparator.EL.thenComparing((java.util.Comparator) comparator, (java.util.Comparator) other));
        }

        public java.util.Comparator<T> reversed() {
            boolean z = !this.nullFirst;
            java.util.Comparator<T> comparator = this.real;
            return new NullComparator(z, comparator == null ? null : Comparator.EL.reversed(comparator));
        }
    }
}
