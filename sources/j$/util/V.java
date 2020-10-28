package j$.util;

import java.util.Iterator;

public final class V {
    private static final Spliterator a = new P();
    private static final D b = new N();
    private static final E c = new O();
    private static final C d = new M();

    private static void a(int i, int i2, int i3) {
        if (i2 > i3) {
            throw new ArrayIndexOutOfBoundsException("origin(" + i2 + ") > fence(" + i3 + ")");
        } else if (i2 < 0) {
            throw new ArrayIndexOutOfBoundsException(i2);
        } else if (i3 > i) {
            throw new ArrayIndexOutOfBoundsException(i3);
        }
    }

    public static C b() {
        return d;
    }

    public static D c() {
        return b;
    }

    public static E d() {
        return c;
    }

    public static Spliterator e() {
        return a;
    }

    public static x f(C c2) {
        c2.getClass();
        return new J(c2);
    }

    public static y g(D d2) {
        d2.getClass();
        return new H(d2);
    }

    public static z h(E e) {
        e.getClass();
        return new I(e);
    }

    public static Iterator i(Spliterator spliterator) {
        spliterator.getClass();
        return new G(spliterator);
    }

    public static C j(double[] dArr, int i, int i2, int i3) {
        dArr.getClass();
        a(dArr.length, i, i2);
        return new L(dArr, i, i2, i3);
    }

    public static D k(int[] iArr, int i, int i2, int i3) {
        iArr.getClass();
        a(iArr.length, i, i2);
        return new S(iArr, i, i2, i3);
    }

    public static E l(long[] jArr, int i, int i2, int i3) {
        jArr.getClass();
        a(jArr.length, i, i2);
        return new U(jArr, i, i2, i3);
    }

    public static Spliterator m(Object[] objArr, int i, int i2, int i3) {
        objArr.getClass();
        a(objArr.length, i, i2);
        return new K(objArr, i, i2, i3);
    }

    public static Spliterator n(Iterator it, int i) {
        it.getClass();
        return new T(it, i);
    }
}
