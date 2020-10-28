package j$.util;

import java.lang.reflect.Field;
import java.util.DoubleSummaryStatistics;

/* renamed from: j$.util.o  reason: case insensitive filesystem */
public class CLASSNAMEo {
    private static final Field a;
    private static final Field b;
    private static final Field c;
    private static final Field d;
    private static final Field e;
    private static final Field f;
    private static final Field g;
    private static final Field h;

    static {
        Class<CLASSNAMEn> cls = CLASSNAMEn.class;
        Field c2 = c(cls, "count");
        a = c2;
        c2.setAccessible(true);
        Field c3 = c(cls, "sum");
        b = c3;
        c3.setAccessible(true);
        Field c4 = c(cls, "min");
        c = c4;
        c4.setAccessible(true);
        Field c5 = c(cls, "max");
        d = c5;
        c5.setAccessible(true);
        Field c6 = c(DoubleSummaryStatistics.class, "count");
        e = c6;
        c6.setAccessible(true);
        Field c7 = c(DoubleSummaryStatistics.class, "sum");
        f = c7;
        c7.setAccessible(true);
        Field c8 = c(DoubleSummaryStatistics.class, "min");
        g = c8;
        c8.setAccessible(true);
        Field c9 = c(DoubleSummaryStatistics.class, "max");
        h = c9;
        c9.setAccessible(true);
    }

    public static CLASSNAMEn a(DoubleSummaryStatistics doubleSummaryStatistics) {
        if (doubleSummaryStatistics == null) {
            return null;
        }
        CLASSNAMEn nVar = new CLASSNAMEn();
        try {
            a.set(nVar, Long.valueOf(doubleSummaryStatistics.getCount()));
            b.set(nVar, Double.valueOf(doubleSummaryStatistics.getSum()));
            c.set(nVar, Double.valueOf(doubleSummaryStatistics.getMin()));
            d.set(nVar, Double.valueOf(doubleSummaryStatistics.getMax()));
            return nVar;
        } catch (IllegalAccessException e2) {
            throw new Error("Failed summary statistics conversion.", e2);
        }
    }

    public static DoubleSummaryStatistics b(CLASSNAMEn nVar) {
        if (nVar == null) {
            return null;
        }
        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        try {
            e.set(doubleSummaryStatistics, Long.valueOf(nVar.c()));
            f.set(doubleSummaryStatistics, Double.valueOf(nVar.f()));
            g.set(doubleSummaryStatistics, Double.valueOf(nVar.e()));
            h.set(doubleSummaryStatistics, Double.valueOf(nVar.d()));
            return doubleSummaryStatistics;
        } catch (IllegalAccessException e2) {
            throw new Error("Failed summary statistics conversion.", e2);
        }
    }

    private static Field c(Class cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e2) {
            throw new Error("Failed summary statistics set-up.", e2);
        }
    }
}
