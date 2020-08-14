package j$.util;

import java.lang.reflect.Field;
import java.util.IntSummaryStatistics;

/* renamed from: j$.util.t  reason: case insensitive filesystem */
public class CLASSNAMEt {
    private static final Field a;
    private static final Field b;
    private static final Field c;
    private static final Field d;

    static {
        Class<CLASSNAMEs> cls = CLASSNAMEs.class;
        b(cls, "count").setAccessible(true);
        b(cls, "sum").setAccessible(true);
        b(cls, "min").setAccessible(true);
        b(cls, "max").setAccessible(true);
        Class<IntSummaryStatistics> cls2 = IntSummaryStatistics.class;
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

    public static IntSummaryStatistics a(CLASSNAMEs stats) {
        if (stats == null) {
            return null;
        }
        IntSummaryStatistics newInstance = new IntSummaryStatistics();
        try {
            a.set(newInstance, Long.valueOf(stats.d()));
            b.set(newInstance, Long.valueOf(stats.g()));
            c.set(newInstance, Integer.valueOf(stats.f()));
            d.set(newInstance, Integer.valueOf(stats.e()));
            return newInstance;
        } catch (IllegalAccessException e) {
            throw new Error("Failed summary statistics conversion.", e);
        }
    }
}
