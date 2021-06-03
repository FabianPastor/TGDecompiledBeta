package j$.util;

import j$.time.a;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Collections;

public interface Comparator<T> {
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
        public static java.util.Comparator $default$reversed(java.util.Comparator comparator) {
            return Collections.reverseOrder(comparator);
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator comparator, Function function) {
            function.getClass();
            return a.D(comparator, new a(function));
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator comparator, java.util.Comparator comparator2) {
            comparator2.getClass();
            return new d(comparator, comparator2);
        }

        public static java.util.Comparator $default$thenComparingDouble(java.util.Comparator comparator, ToDoubleFunction toDoubleFunction) {
            toDoubleFunction.getClass();
            return a.D(comparator, new c(toDoubleFunction));
        }

        public static java.util.Comparator $default$thenComparingInt(java.util.Comparator comparator, ToIntFunction toIntFunction) {
            toIntFunction.getClass();
            return a.D(comparator, new e(toIntFunction));
        }

        public static java.util.Comparator $default$thenComparingLong(java.util.Comparator comparator, ToLongFunction toLongFunction) {
            toLongFunction.getClass();
            return a.D(comparator, new f(toLongFunction));
        }

        public static java.util.Comparator a() {
            return k.INSTANCE;
        }

        public static java.util.Comparator reverseOrder() {
            return Collections.reverseOrder();
        }

        public static java.util.Comparator $default$thenComparing(java.util.Comparator comparator, Function function, java.util.Comparator comparator2) {
            function.getClass();
            comparator2.getClass();
            return a.D(comparator, new b(comparator2, function));
        }
    }
}
