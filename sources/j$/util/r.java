package j$.util;

import java.lang.reflect.Field;
import java.util.DoubleSummaryStatistics;

public class r {
    private static final Field a;
    private static final Field b;
    private static final Field c;
    private static final Field d;

    static {
        Class<CLASSNAMEq> cls = CLASSNAMEq.class;
        b(cls, "count").setAccessible(true);
        b(cls, "sum").setAccessible(true);
        b(cls, "min").setAccessible(true);
        b(cls, "max").setAccessible(true);
        Class<DoubleSummaryStatistics> cls2 = DoubleSummaryStatistics.class;
        Field b2 = b(cls2, "count");
        a = b2;
        b2.setAccessible(true);
        Field b3 = b(cls2, "sum");
        b = b3;
        b3.setAccessible(true);
        Field b4 = b(cls2, "min");
        c = b4;
        b4.setAccessible(true);
        Field b5 = b(cls2, "max");
        d = b5;
        b5.setAccessible(true);
    }

    private static Field b(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new Error("Failed summary statistics set-up.", e);
        }
    }

    public static DoubleSummaryStatistics a(CLASSNAMEq stats) {
        if (stats == null) {
            return null;
        }
        DoubleSummaryStatistics newInstance = new DoubleSummaryStatistics();
        try {
            a.set(newInstance, Long.valueOf(stats.d()));
            b.set(newInstance, Double.valueOf(stats.g()));
            c.set(newInstance, Double.valueOf(stats.f()));
            d.set(newInstance, Double.valueOf(stats.e()));
            return newInstance;
        } catch (IllegalAccessException e) {
            throw new Error("Failed summary statistics conversion.", e);
        }
    }
}
