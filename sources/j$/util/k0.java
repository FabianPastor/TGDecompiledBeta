package j$.util;

import java.util.Collection;
import java.util.Iterator;

public final class k0 {
    private static final Spliterator a = new f0();
    private static final S b = new d0();
    private static final U c = new e0();
    private static final P d = new c0();

    public static Spliterator e() {
        return a;
    }

    public static S c() {
        return b;
    }

    public static U d() {
        return c;
    }

    public static P b() {
        return d;
    }

    public static Spliterator n(Object[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        a(array.length, fromIndex, toIndex);
        return new a0(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static S k(int[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        a(array.length, fromIndex, toIndex);
        return new h0(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static U l(long[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        a(array.length, fromIndex, toIndex);
        return new j0(array, fromIndex, toIndex, additionalCharacteristics);
    }

    public static P j(double[] array, int fromIndex, int toIndex, int additionalCharacteristics) {
        array.getClass();
        a(array.length, fromIndex, toIndex);
        return new b0(array, fromIndex, toIndex, additionalCharacteristics);
    }

    private static void a(int arrayLength, int origin, int fence) {
        if (origin > fence) {
            throw new ArrayIndexOutOfBoundsException("origin(" + origin + ") > fence(" + fence + ")");
        } else if (origin < 0) {
            throw new ArrayIndexOutOfBoundsException(origin);
        } else if (fence > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(fence);
        }
    }

    public static Spliterator m(Collection c2, int characteristics) {
        c2.getClass();
        return new i0(c2, characteristics);
    }

    public static Spliterator o(Iterator iterator, int characteristics) {
        iterator.getClass();
        return new i0(iterator, characteristics);
    }

    public static Iterator i(Spliterator spliterator) {
        spliterator.getClass();
        return new W(spliterator);
    }

    public static H g(S spliterator) {
        spliterator.getClass();
        return new X(spliterator);
    }

    public static J h(U spliterator) {
        spliterator.getClass();
        return new Y(spliterator);
    }

    public static F f(P spliterator) {
        spliterator.getClass();
        return new Z(spliterator);
    }
}
