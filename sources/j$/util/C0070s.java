package j$.util;

import java.lang.reflect.Field;
import java.util.LongSummaryStatistics;

/* renamed from: j$.util.s  reason: case insensitive filesystem */
public class CLASSNAMEs {
    private static final Field a;
    private static final Field b;
    private static final Field c;
    private static final Field d;
    private static final Field e;
    private static final Field f;
    private static final Field g;
    private static final Field h;

    static {
        Class<r> cls = r.class;
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
        Field c6 = c(LongSummaryStatistics.class, "count");
        e = c6;
        c6.setAccessible(true);
        Field c7 = c(LongSummaryStatistics.class, "sum");
        f = c7;
        c7.setAccessible(true);
        Field c8 = c(LongSummaryStatistics.class, "min");
        g = c8;
        c8.setAccessible(true);
        Field c9 = c(LongSummaryStatistics.class, "max");
        h = c9;
        c9.setAccessible(true);
    }

    public static r a(LongSummaryStatistics longSummaryStatistics) {
        if (longSummaryStatistics == null) {
            return null;
        }
        r rVar = new r();
        try {
            a.set(rVar, Long.valueOf(longSummaryStatistics.getCount()));
            b.set(rVar, Long.valueOf(longSummaryStatistics.getSum()));
            c.set(rVar, Long.valueOf(longSummaryStatistics.getMin()));
            d.set(rVar, Long.valueOf(longSummaryStatistics.getMax()));
            return rVar;
        } catch (IllegalAccessException e2) {
            throw new Error("Failed summary statistics conversion.", e2);
        }
    }

    public static LongSummaryStatistics b(r rVar) {
        if (rVar == null) {
            return null;
        }
        LongSummaryStatistics longSummaryStatistics = new LongSummaryStatistics();
        try {
            e.set(longSummaryStatistics, Long.valueOf(rVar.c()));
            f.set(longSummaryStatistics, Long.valueOf(rVar.f()));
            g.set(longSummaryStatistics, Long.valueOf(rVar.e()));
            h.set(longSummaryStatistics, Long.valueOf(rVar.d()));
            return longSummaryStatistics;
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
