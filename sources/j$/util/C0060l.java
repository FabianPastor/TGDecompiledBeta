package j$.util;

import j$.util.Comparator;
import java.util.Comparator;

/* renamed from: j$.util.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl {
    public static /* synthetic */ Comparator a(Comparator comparator, Comparator comparator2) {
        return comparator instanceof Comparator ? ((Comparator) comparator).thenComparing(comparator2) : Comparator.CC.$default$thenComparing(comparator, comparator2);
    }
}
