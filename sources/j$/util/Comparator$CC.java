package j$.util;

import java.util.Collections;
import java.util.Comparator;

/* renamed from: j$.util.Comparator$-CC  reason: invalid class name */
public final /* synthetic */ class Comparator$CC {
    public static Comparator a() {
        return CLASSNAMEf.INSTANCE;
    }

    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }
}
