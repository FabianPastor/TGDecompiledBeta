package j$.util;

import java.util.Iterator;

public abstract class N {
    private static final y a = new I();
    private static final v b = new G();
    private static final w c = new H();
    private static final u d = new F();

    private static void a(int i, int i2, int i3) {
        if (i2 > i3) {
            throw new ArrayIndexOutOfBoundsException("origin(" + i2 + ") > fence(" + i3 + ")");
        } else if (i2 < 0) {
            throw new ArrayIndexOutOfBoundsException(i2);
        } else if (i3 > i) {
            throw new ArrayIndexOutOfBoundsException(i3);
        }
    }

    public static u b() {
        return d;
    }

    public static v c() {
        return b;
    }

    public static w d() {
        return c;
    }

    public static y e() {
        return a;
    }

    public static CLASSNAMEn f(u uVar) {
        uVar.getClass();
        return new C(uVar);
    }

    public static CLASSNAMEp g(v vVar) {
        vVar.getClass();
        return new A(vVar);
    }

    public static r h(w wVar) {
        wVar.getClass();
        return new B(wVar);
    }

    public static Iterator i(y yVar) {
        yVar.getClass();
        return new z(yVar);
    }

    public static u j(double[] dArr, int i, int i2, int i3) {
        dArr.getClass();
        a(dArr.length, i, i2);
        return new E(dArr, i, i2, i3);
    }

    public static v k(int[] iArr, int i, int i2, int i3) {
        iArr.getClass();
        a(iArr.length, i, i2);
        return new K(iArr, i, i2, i3);
    }

    public static w l(long[] jArr, int i, int i2, int i3) {
        jArr.getClass();
        a(jArr.length, i, i2);
        return new M(jArr, i, i2, i3);
    }

    public static y m(Object[] objArr, int i, int i2, int i3) {
        objArr.getClass();
        a(objArr.length, i, i2);
        return new D(objArr, i, i2, i3);
    }
}
