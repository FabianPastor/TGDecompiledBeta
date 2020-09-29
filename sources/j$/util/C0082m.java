package j$.util;

import j$.util.Comparator;
import java.util.Comparator;

/* renamed from: j$.util.m  reason: case insensitive filesystem */
enum CLASSNAMEm implements Comparator, Comparator {
    INSTANCE;

    /* renamed from: i */
    public int compare(Comparable c1, Comparable c2) {
        return c1.compareTo(c2);
    }

    public Comparator reversed() {
        return Comparator.CC.m();
    }
}
