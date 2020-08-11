package j$.util.function;

import java.util.Comparator;

/* renamed from: j$.util.function.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn {
    public static CLASSNAMEo d(Comparator comparator) {
        comparator.getClass();
        return new CLASSNAMEc(comparator);
    }

    public static /* synthetic */ Object b(Comparator comparator, Object a, Object b) {
        return comparator.compare(a, b) <= 0 ? a : b;
    }

    public static CLASSNAMEo c(Comparator comparator) {
        comparator.getClass();
        return new CLASSNAMEb(comparator);
    }

    public static /* synthetic */ Object a(Comparator comparator, Object a, Object b) {
        return comparator.compare(a, b) >= 0 ? a : b;
    }
}
