package j$.util;

import java.lang.reflect.Field;
import java.util.IntSummaryStatistics;

/* renamed from: j$.util.q  reason: case insensitive filesystem */
public class CLASSNAMEq {
    private static final Field a;
    private static final Field b;
    private static final Field c;
    private static final Field d;
    private static final Field e;
    private static final Field f;
    private static final Field g;
    private static final Field h;

    static {
        Class<CLASSNAMEp> cls = CLASSNAMEp.class;
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
        Field c6 = c(IntSummaryStatistics.class, "count");
        e = c6;
        c6.setAccessible(true);
        Field c7 = c(IntSummaryStatistics.class, "sum");
        f = c7;
        c7.setAccessible(true);
        Field c8 = c(IntSummaryStatistics.class, "min");
        g = c8;
        c8.setAccessible(true);
        Field c9 = c(IntSummaryStatistics.class, "max");
        h = c9;
        c9.setAccessible(true);
    }

    public static CLASSNAMEp a(IntSummaryStatistics intSummaryStatistics) {
        if (intSummaryStatistics == null) {
            return null;
        }
        CLASSNAMEp pVar = new CLASSNAMEp();
        try {
            a.set(pVar, Long.valueOf(intSummaryStatistics.getCount()));
            b.set(pVar, Long.valueOf(intSummaryStatistics.getSum()));
            c.set(pVar, Integer.valueOf(intSummaryStatistics.getMin()));
            d.set(pVar, Integer.valueOf(intSummaryStatistics.getMax()));
            return pVar;
        } catch (IllegalAccessException e2) {
            throw new Error("Failed summary statistics conversion.", e2);
        }
    }

    public static IntSummaryStatistics b(CLASSNAMEp pVar) {
        if (pVar == null) {
            return null;
        }
        IntSummaryStatistics intSummaryStatistics = new IntSummaryStatistics();
        try {
            e.set(intSummaryStatistics, Long.valueOf(pVar.c()));
            f.set(intSummaryStatistics, Long.valueOf(pVar.f()));
            g.set(intSummaryStatistics, Integer.valueOf(pVar.e()));
            h.set(intSummaryStatistics, Integer.valueOf(pVar.d()));
            return intSummaryStatistics;
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
