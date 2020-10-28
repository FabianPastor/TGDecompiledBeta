package j$.util.stream;

import j$.CLASSNAMEm0;
import j$.H;
import j$.Y;
import j$.util.C;
import j$.util.D;
import j$.util.E;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.q;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.y;

/* renamed from: j$.util.stream.c3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc3 {
    public static void a(CLASSNAMEq5 q5Var, Double d) {
        if (!L6.a) {
            q5Var.accept(d.doubleValue());
        } else {
            L6.a(q5Var.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(CLASSNAMEr5 r5Var, Integer num) {
        if (!L6.a) {
            r5Var.accept(num.intValue());
        } else {
            L6.a(r5Var.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(CLASSNAMEs5 s5Var, Long l) {
        if (!L6.a) {
            s5Var.accept(l.longValue());
        } else {
            L6.a(s5Var.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static Object[] d(CLASSNAMEk3 k3Var, v vVar) {
        if (L6.a) {
            L6.a(k3Var.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (k3Var.count() < NUM) {
            Object[] objArr = (Object[]) vVar.apply((int) k3Var.count());
            k3Var.j(objArr, 0);
            return objArr;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static void e(CLASSNAMEh3 h3Var, Double[] dArr, int i) {
        if (!L6.a) {
            double[] dArr2 = (double[]) h3Var.e();
            for (int i2 = 0; i2 < dArr2.length; i2++) {
                dArr[i + i2] = Double.valueOf(dArr2[i2]);
            }
            return;
        }
        L6.a(h3Var.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
        throw null;
    }

    public static void f(CLASSNAMEi3 i3Var, Integer[] numArr, int i) {
        if (!L6.a) {
            int[] iArr = (int[]) i3Var.e();
            for (int i2 = 0; i2 < iArr.length; i2++) {
                numArr[i + i2] = Integer.valueOf(iArr[i2]);
            }
            return;
        }
        L6.a(i3Var.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
        throw null;
    }

    public static void g(CLASSNAMEj3 j3Var, Long[] lArr, int i) {
        if (!L6.a) {
            long[] jArr = (long[]) j3Var.e();
            for (int i2 = 0; i2 < jArr.length; i2++) {
                lArr[i + i2] = Long.valueOf(jArr[i2]);
            }
            return;
        }
        L6.a(j3Var.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
        throw null;
    }

    public static void h(CLASSNAMEh3 h3Var, Consumer consumer) {
        if (consumer instanceof q) {
            h3Var.h((q) consumer);
        } else if (!L6.a) {
            ((C) h3Var.spliterator()).forEachRemaining(consumer);
        } else {
            L6.a(h3Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void i(CLASSNAMEi3 i3Var, Consumer consumer) {
        if (consumer instanceof u) {
            i3Var.h((u) consumer);
        } else if (!L6.a) {
            ((D) i3Var.spliterator()).forEachRemaining(consumer);
        } else {
            L6.a(i3Var.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void j(CLASSNAMEj3 j3Var, Consumer consumer) {
        if (consumer instanceof y) {
            j3Var.h((y) consumer);
        } else if (!L6.a) {
            ((E) j3Var.spliterator()).forEachRemaining(consumer);
        } else {
            L6.a(j3Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static CLASSNAMEh3 k(CLASSNAMEh3 h3Var, long j, long j2, v vVar) {
        if (j == 0 && j2 == h3Var.count()) {
            return h3Var;
        }
        long j3 = j2 - j;
        C c = (C) h3Var.spliterator();
        CLASSNAMEd3 j4 = CLASSNAMEh4.j(j3);
        j4.n(j3);
        for (int i = 0; ((long) i) < j && c.o(CLASSNAMEi0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && c.o(j4); i2++) {
        }
        j4.m();
        return j4.a();
    }

    public static CLASSNAMEi3 l(CLASSNAMEi3 i3Var, long j, long j2, v vVar) {
        if (j == 0 && j2 == i3Var.count()) {
            return i3Var;
        }
        long j3 = j2 - j;
        D d = (D) i3Var.spliterator();
        CLASSNAMEe3 p = CLASSNAMEh4.p(j3);
        p.n(j3);
        for (int i = 0; ((long) i) < j && d.h(CLASSNAMEj0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && d.h(p); i2++) {
        }
        p.m();
        return p.a();
    }

    public static CLASSNAMEj3 m(CLASSNAMEj3 j3Var, long j, long j2, v vVar) {
        if (j == 0 && j2 == j3Var.count()) {
            return j3Var;
        }
        long j3 = j2 - j;
        E e = (E) j3Var.spliterator();
        CLASSNAMEf3 q = CLASSNAMEh4.q(j3);
        q.n(j3);
        for (int i = 0; ((long) i) < j && e.j(CLASSNAMEk0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && e.j(q); i2++) {
        }
        q.m();
        return q.a();
    }

    public static CLASSNAMEl3 n(CLASSNAMEl3 l3Var, long j, long j2, v vVar) {
        if (j == 0 && j2 == l3Var.count()) {
            return l3Var;
        }
        Spliterator spliterator = l3Var.spliterator();
        long j3 = j2 - j;
        CLASSNAMEg3 d = CLASSNAMEh4.d(j3, vVar);
        d.n(j3);
        for (int i = 0; ((long) i) < j && spliterator.b(CLASSNAMEh0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && spliterator.b(d); i2++) {
        }
        d.m();
        return d.a();
    }

    public static L1 o(C c, boolean z) {
        return new H1(c, CLASSNAMEg6.c(c), z);
    }

    public static CLASSNAMEx2 p(D d, boolean z) {
        return new CLASSNAMEt2(d, CLASSNAMEg6.c(d), z);
    }

    public static T2 q(E e, boolean z) {
        return new P2(e, CLASSNAMEg6.c(e), z);
    }

    public static J6 r(H h, Z2 z2) {
        h.getClass();
        return new CLASSNAMEa3(CLASSNAMEh6.DOUBLE_VALUE, z2, new CLASSNAMEc0(z2, h));
    }

    public static J6 s(Y y, Z2 z2) {
        y.getClass();
        return new CLASSNAMEa3(CLASSNAMEh6.INT_VALUE, z2, new CLASSNAMEe0(z2, y));
    }

    public static J6 t(CLASSNAMEm0 m0Var, Z2 z2) {
        m0Var.getClass();
        return new CLASSNAMEa3(CLASSNAMEh6.LONG_VALUE, z2, new CLASSNAMEf0(z2, m0Var));
    }

    public static J6 u(Predicate predicate, Z2 z2) {
        predicate.getClass();
        return new CLASSNAMEa3(CLASSNAMEh6.REFERENCE, z2, new CLASSNAMEd0(z2, predicate));
    }

    public static Stream v(Spliterator spliterator, boolean z) {
        spliterator.getClass();
        return new CLASSNAMEi5(spliterator, CLASSNAMEg6.c(spliterator), z);
    }
}
