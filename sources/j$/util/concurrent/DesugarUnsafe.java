package j$.util.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

final class DesugarUnsafe {
    private static final Unsafe theUnsafe;

    DesugarUnsafe() {
    }

    static {
        Field field = getField();
        field.setAccessible(true);
        try {
            theUnsafe = (Unsafe) field.get((Object) null);
        } catch (IllegalAccessException e) {
            throw new Error("Couldn't get the Unsafe", e);
        }
    }

    private static Field getField() {
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

    public static Unsafe getUnsafe() {
        return theUnsafe;
    }

    public static final int getAndAddInt(Unsafe unsafe, Object o, long offset, int delta) {
        int v;
        do {
            v = unsafe.getIntVolatile(o, offset);
        } while (!unsafe.compareAndSwapInt(o, offset, v, v + delta));
        return v;
    }

    public static final long getAndAddLong(Unsafe unsafe, Object o, long offset, long delta) {
        long v;
        do {
            v = unsafe.getLongVolatile(o, offset);
        } while (!unsafe.compareAndSwapLong(o, offset, v, v + delta));
        return v;
    }

    public static final int getAndSetInt(Unsafe unsafe, Object o, long offset, int newValue) {
        int v;
        do {
            v = unsafe.getIntVolatile(o, offset);
        } while (!unsafe.compareAndSwapInt(o, offset, v, newValue));
        return v;
    }

    public static final long getAndSetLong(Unsafe unsafe, Object o, long offset, long newValue) {
        long v;
        do {
            v = unsafe.getLongVolatile(o, offset);
        } while (!unsafe.compareAndSwapLong(o, offset, v, newValue));
        return v;
    }

    public static final Object getAndSetObject(Unsafe unsafe, Object o, long offset, Object newValue) {
        Object v;
        do {
            v = unsafe.getObjectVolatile(o, offset);
        } while (!unsafe.compareAndSwapObject(o, offset, v, newValue));
        return v;
    }
}
