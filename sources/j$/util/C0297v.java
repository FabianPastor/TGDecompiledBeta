package j$.util;

import j$.util.List;
import j$.util.function.UnaryOperator;
import java.util.Comparator;
import java.util.List;

/* renamed from: j$.util.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv {
    public static /* synthetic */ void a(List list, UnaryOperator unaryOperator) {
        if (list instanceof List) {
            ((List) list).replaceAll(unaryOperator);
        } else {
            List.CC.$default$replaceAll(list, unaryOperator);
        }
    }

    public static /* synthetic */ void b(java.util.List list, Comparator comparator) {
        if (list instanceof List) {
            ((List) list).sort(comparator);
        } else {
            List.CC.$default$sort(list, comparator);
        }
    }
}
