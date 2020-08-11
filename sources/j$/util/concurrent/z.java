package j$.util.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

final class z {
    private static final Unsafe a;

    static {
        Field field = b();
        field.setAccessible(true);
        try {
            a = (Unsafe) field.get((Object) null);
        } catch (IllegalAccessException e) {
            throw new Error("Couldn't get the Unsafe", e);
        }
    }

    private static Field b() {
        try {
            return Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            for (Field f : Unsafe.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    if (Unsafe.class.isAssignableFrom(f.getType())) {
                        return f;
                    }
                }
            }
            throw new Error("Couldn't find the Unsafe", e);
        }
    }

    public static Unsafe c() {
        return a;
    }

    public static final int a(Unsafe unsafe, Object o, long offset, int delta) {
        int v;
        do {
            v = unsafe.getIntVolatile(o, offset);
        } while (!unsafe.compareAndSwapInt(o, offset, v, v + delta));
        return v;
    }
}
