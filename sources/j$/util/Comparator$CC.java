package j$.util;

import j$.util.function.Function;
import java.util.Collections;
import java.util.Comparator;

/* renamed from: j$.util.Comparator$-CC  reason: invalid class name */
public final /* synthetic */ class Comparator$CC {
    public static Comparator a() {
        return CLASSNAMEf.INSTANCE;
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> function) {
        function.getClass();
        return new CLASSNAMEd((Function) function);
    }

    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }
}