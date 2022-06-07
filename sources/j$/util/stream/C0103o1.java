package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.f;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.q;
import j$.util.t;
import j$.util.u;
import j$.util.v;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.E;
import j$.wrappers.V;

/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
public abstract /* synthetic */ class CLASSNAMEo1 {
    public static void a(CLASSNAMEj3 j3Var, Double d) {
        if (!Q4.a) {
            j3Var.accept(d.doubleValue());
        } else {
            Q4.a(j3Var.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(CLASSNAMEk3 k3Var, Integer num) {
        if (!Q4.a) {
            k3Var.accept(num.intValue());
        } else {
            Q4.a(k3Var.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(CLASSNAMEl3 l3Var, Long l) {
        if (!Q4.a) {
            l3Var.accept(l.longValue());
        } else {
            Q4.a(l3Var.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(CLASSNAMEm3 m3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(CLASSNAMEm3 m3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(CLASSNAMEm3 m3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(CLASSNAMEz1 z1Var, m mVar) {
        if (Q4.a) {
            Q4.a(z1Var.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (z1Var.count() < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) z1Var.count());
            z1Var.i(objArr, 0);
            return objArr;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static void h(CLASSNAMEu1 u1Var, Double[] dArr, int i) {
        if (!Q4.a) {
            double[] dArr2 = (double[]) u1Var.e();
            for (int i2 = 0; i2 < dArr2.length; i2++) {
                dArr[i + i2] = Double.valueOf(dArr2[i2]);
            }
            return;
        }
        Q4.a(u1Var.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
        throw null;
    }

    public static void i(CLASSNAMEw1 w1Var, Integer[] numArr, int i) {
        if (!Q4.a) {
            int[] iArr = (int[]) w1Var.e();
            for (int i2 = 0; i2 < iArr.length; i2++) {
                numArr[i + i2] = Integer.valueOf(iArr[i2]);
            }
            return;
        }
        Q4.a(w1Var.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
        throw null;
    }

    public static void j(CLASSNAMEy1 y1Var, Long[] lArr, int i) {
        if (!Q4.a) {
            long[] jArr = (long[]) y1Var.e();
            for (int i2 = 0; i2 < jArr.length; i2++) {
                lArr[i + i2] = Long.valueOf(jArr[i2]);
            }
            return;
        }
        Q4.a(y1Var.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
        throw null;
    }

    public static void k(CLASSNAMEu1 u1Var, Consumer consumer) {
        if (consumer instanceof f) {
            u1Var.g((f) consumer);
        } else if (!Q4.a) {
            ((t) u1Var.spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(u1Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(CLASSNAMEw1 w1Var, Consumer consumer) {
        if (consumer instanceof l) {
            w1Var.g((l) consumer);
        } else if (!Q4.a) {
            ((u.a) w1Var.spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(w1Var.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(CLASSNAMEy1 y1Var, Consumer consumer) {
        if (consumer instanceof q) {
            y1Var.g((q) consumer);
        } else if (!Q4.a) {
            ((v) y1Var.spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(y1Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static CLASSNAMEu1 n(CLASSNAMEu1 u1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == u1Var.count()) {
            return u1Var;
        }
        long j3 = j2 - j;
        t tVar = (t) u1Var.spliterator();
        CLASSNAMEp1 j4 = CLASSNAMEx2.j(j3);
        j4.n(j3);
        for (int i = 0; ((long) i) < j && tVar.k(CLASSNAMEt1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && tVar.k(j4); i2++) {
        }
        j4.m();
        return j4.a();
    }

    public static CLASSNAMEw1 o(CLASSNAMEw1 w1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == w1Var.count()) {
            return w1Var;
        }
        long j3 = j2 - j;
        u.a aVar = (u.a) w1Var.spliterator();
        CLASSNAMEq1 p = CLASSNAMEx2.p(j3);
        p.n(j3);
        for (int i = 0; ((long) i) < j && aVar.g(CLASSNAMEv1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && aVar.g(p); i2++) {
        }
        p.m();
        return p.a();
    }

    public static CLASSNAMEy1 p(CLASSNAMEy1 y1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == y1Var.count()) {
            return y1Var;
        }
        long j3 = j2 - j;
        v vVar = (v) y1Var.spliterator();
        CLASSNAMEr1 q = CLASSNAMEx2.q(j3);
        q.n(j3);
        for (int i = 0; ((long) i) < j && vVar.i(CLASSNAMEx1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && vVar.i(q); i2++) {
        }
        q.m();
        return q.a();
    }

    public static A1 q(A1 a1, long j, long j2, m mVar) {
        if (j == 0 && j2 == a1.count()) {
            return a1;
        }
        u spliterator = a1.spliterator();
        long j3 = j2 - j;
        CLASSNAMEs1 d = CLASSNAMEx2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; ((long) i) < j && spliterator.b(CLASSNAMEn1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && spliterator.b(d); i2++) {
        }
        d.m();
        return d.a();
    }

    public static U r(t tVar, boolean z) {
        return new P(tVar, CLASSNAMEd4.c(tVar), z);
    }

    public static IntStream s(u.a aVar, boolean z) {
        return new I0(aVar, CLASSNAMEd4.c(aVar), z);
    }

    public static CLASSNAMEe1 t(v vVar, boolean z) {
        return new CLASSNAMEa1(vVar, CLASSNAMEd4.c(vVar), z);
    }

    public static N4 u(E e, CLASSNAMEk1 k1Var) {
        e.getClass();
        k1Var.getClass();
        return new CLASSNAMEl1(CLASSNAMEe4.DOUBLE_VALUE, k1Var, new CLASSNAMEo(k1Var, e));
    }

    public static N4 v(V v, CLASSNAMEk1 k1Var) {
        v.getClass();
        k1Var.getClass();
        return new CLASSNAMEl1(CLASSNAMEe4.INT_VALUE, k1Var, new CLASSNAMEo(k1Var, v));
    }

    public static N4 w(CLASSNAMEj0 j0Var, CLASSNAMEk1 k1Var) {
        j0Var.getClass();
        k1Var.getClass();
        return new CLASSNAMEl1(CLASSNAMEe4.LONG_VALUE, k1Var, new CLASSNAMEo(k1Var, j0Var));
    }

    public static N4 x(Predicate predicate, CLASSNAMEk1 k1Var) {
        predicate.getClass();
        k1Var.getClass();
        return new CLASSNAMEl1(CLASSNAMEe4.REFERENCE, k1Var, new CLASSNAMEo(k1Var, predicate));
    }

    public static Stream y(u uVar, boolean z) {
        uVar.getClass();
        return new CLASSNAMEb3(uVar, CLASSNAMEd4.c(uVar), z);
    }
}
