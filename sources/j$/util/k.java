package j$.util;

import j$.util.Comparator;
import java.util.Comparator;

enum k implements Comparator<Comparable<Object>>, Comparator {
    INSTANCE;

    public int compare(Object obj, Object obj2) {
        return ((Comparable) obj).compareTo((Comparable) obj2);
    }

    public Comparator reversed() {
        return Comparator.CC.reverseOrder();
    }
}
