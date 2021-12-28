package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.f;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.q;
import j$.util.u;
import j$.util.v;
import j$.util.w;
import j$.util.y;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.E;
import j$.wrappers.V;

/* renamed from: j$.util.stream.p1  reason: case insensitive filesystem */
public abstract /* synthetic */ class CLASSNAMEp1 {
    public static void a(CLASSNAMEk3 k3Var, Double d) {
        if (!R4.a) {
            k3Var.accept(d.doubleValue());
        } else {
            R4.a(k3Var.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(CLASSNAMEl3 l3Var, Integer num) {
        if (!R4.a) {
            l3Var.accept(num.intValue());
        } else {
            R4.a(l3Var.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(CLASSNAMEm3 m3Var, Long l) {
        if (!R4.a) {
            m3Var.accept(l.longValue());
        } else {
            R4.a(m3Var.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(CLASSNAMEn3 n3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(CLASSNAMEn3 n3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(CLASSNAMEn3 n3Var) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(A1 a1, m mVar) {
        if (R4.a) {
            R4.a(a1.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (a1.count() < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) a1.count());
            a1.i(objArr, 0);
            return objArr;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static void h(CLASSNAMEv1 v1Var, Double[] dArr, int i) {
        if (!R4.a) {
            double[] dArr2 = (double[]) v1Var.e();
            for (int i2 = 0; i2 < dArr2.length; i2++) {
                dArr[i + i2] = Double.valueOf(dArr2[i2]);
            }
            return;
        }
        R4.a(v1Var.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
        throw null;
    }

    public static void i(CLASSNAMEx1 x1Var, Integer[] numArr, int i) {
        if (!R4.a) {
            int[] iArr = (int[]) x1Var.e();
            for (int i2 = 0; i2 < iArr.length; i2++) {
                numArr[i + i2] = Integer.valueOf(iArr[i2]);
            }
            return;
        }
        R4.a(x1Var.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
        throw null;
    }

    public static void j(CLASSNAMEz1 z1Var, Long[] lArr, int i) {
        if (!R4.a) {
            long[] jArr = (long[]) z1Var.e();
            for (int i2 = 0; i2 < jArr.length; i2++) {
                lArr[i + i2] = Long.valueOf(jArr[i2]);
            }
            return;
        }
        R4.a(z1Var.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
        throw null;
    }

    public static void k(CLASSNAMEv1 v1Var, Consumer consumer) {
        if (consumer instanceof f) {
            v1Var.g((f) consumer);
        } else if (!R4.a) {
            ((u) v1Var.spliterator()).forEachRemaining(consumer);
        } else {
            R4.a(v1Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(CLASSNAMEx1 x1Var, Consumer consumer) {
        if (consumer instanceof l) {
            x1Var.g((l) consumer);
        } else if (!R4.a) {
            ((v) x1Var.spliterator()).forEachRemaining(consumer);
        } else {
            R4.a(x1Var.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(CLASSNAMEz1 z1Var, Consumer consumer) {
        if (consumer instanceof q) {
            z1Var.g((q) consumer);
        } else if (!R4.a) {
            ((w) z1Var.spliterator()).forEachRemaining(consumer);
        } else {
            R4.a(z1Var.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static CLASSNAMEv1 n(CLASSNAMEv1 v1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == v1Var.count()) {
            return v1Var;
        }
        long j3 = j2 - j;
        u uVar = (u) v1Var.spliterator();
        CLASSNAMEq1 j4 = CLASSNAMEy2.j(j3);
        j4.n(j3);
        for (int i = 0; ((long) i) < j && uVar.k(CLASSNAMEu1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && uVar.k(j4); i2++) {
        }
        j4.m();
        return j4.a();
    }

    public static CLASSNAMEx1 o(CLASSNAMEx1 x1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == x1Var.count()) {
            return x1Var;
        }
        long j3 = j2 - j;
        v vVar = (v) x1Var.spliterator();
        CLASSNAMEr1 p = CLASSNAMEy2.p(j3);
        p.n(j3);
        for (int i = 0; ((long) i) < j && vVar.g(CLASSNAMEw1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && vVar.g(p); i2++) {
        }
        p.m();
        return p.a();
    }

    public static CLASSNAMEz1 p(CLASSNAMEz1 z1Var, long j, long j2, m mVar) {
        if (j == 0 && j2 == z1Var.count()) {
            return z1Var;
        }
        long j3 = j2 - j;
        w wVar = (w) z1Var.spliterator();
        CLASSNAMEs1 q = CLASSNAMEy2.q(j3);
        q.n(j3);
        for (int i = 0; ((long) i) < j && wVar.i(CLASSNAMEy1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && wVar.i(q); i2++) {
        }
        q.m();
        return q.a();
    }

    public static B1 q(B1 b1, long j, long j2, m mVar) {
        if (j == 0 && j2 == b1.count()) {
            return b1;
        }
        y spliterator = b1.spliterator();
        long j3 = j2 - j;
        CLASSNAMEt1 d = CLASSNAMEy2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; ((long) i) < j && spliterator.b(CLASSNAMEo1.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && spliterator.b(d); i2++) {
        }
        d.m();
        return d.a();
    }

    public static U r(u uVar, boolean z) {
        return new P(uVar, CLASSNAMEe4.c(uVar), z);
    }

    public static M0 s(v vVar, boolean z) {
        return new I0(vVar, CLASSNAMEe4.c(vVar), z);
    }

    public static CLASSNAMEf1 t(w wVar, boolean z) {
        return new CLASSNAMEb1(wVar, CLASSNAMEe4.c(wVar), z);
    }

    public static O4 u(E e, CLASSNAMEl1 l1Var) {
        e.getClass();
        l1Var.getClass();
        return new CLASSNAMEm1(CLASSNAMEf4.DOUBLE_VALUE, l1Var, new CLASSNAMEo(l1Var, e));
    }

    public static O4 v(V v, CLASSNAMEl1 l1Var) {
        v.getClass();
        l1Var.getClass();
        return new CLASSNAMEm1(CLASSNAMEf4.INT_VALUE, l1Var, new CLASSNAMEo(l1Var, v));
    }

    public static O4 w(CLASSNAMEj0 j0Var, CLASSNAMEl1 l1Var) {
        j0Var.getClass();
        l1Var.getClass();
        return new CLASSNAMEm1(CLASSNAMEf4.LONG_VALUE, l1Var, new CLASSNAMEo(l1Var, j0Var));
    }

    public static O4 x(Predicate predicate, CLASSNAMEl1 l1Var) {
        predicate.getClass();
        l1Var.getClass();
        return new CLASSNAMEm1(CLASSNAMEf4.REFERENCE, l1Var, new CLASSNAMEo(l1Var, predicate));
    }

    public static Stream y(y yVar, boolean z) {
        yVar.getClass();
        return new CLASSNAMEc3(yVar, CLASSNAMEe4.c(yVar), z);
    }
}
