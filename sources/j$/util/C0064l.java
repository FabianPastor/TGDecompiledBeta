package j$.util;

import j$.util.Comparator;
import java.util.Comparator;

/* renamed from: j$.util.l  reason: case insensitive filesystem */
enum CLASSNAMEl implements Comparator, Comparator {
    INSTANCE;

    public int compare(Object obj, Object obj2) {
        return ((Comparable) obj).compareTo((Comparable) obj2);
    }

    public Comparator reversed() {
        return Comparator.CC.reverseOrder();
    }
}
