package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.Predicate;
import j$.util.function.q;
import j$.util.function.s;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.CLASSNAMEz1;
import j$.util.stream.D1;
import j$.util.stream.R1;

public final /* synthetic */ class Q1 {
    public static void a(A2.e eVar, Double d) {
        if (!i3.var_a) {
            eVar.accept(d.doubleValue());
        } else {
            i3.a(eVar.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(A2.f fVar, Integer num) {
        if (!i3.var_a) {
            fVar.accept(num.intValue());
        } else {
            i3.a(fVar.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(A2.g gVar, Long l) {
        if (!i3.var_a) {
            gVar.accept(l.longValue());
        } else {
            i3.a(gVar.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static Object[] d(R1.e eVar, x xVar) {
        if (i3.var_a) {
            i3.a(eVar.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (eVar.count() < NUM) {
            Object[] objArr = (Object[]) xVar.apply((int) eVar.count());
            eVar.j(objArr, 0);
            return objArr;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static void e(R1.b bVar, Double[] dArr, int i) {
        if (!i3.var_a) {
            double[] dArr2 = (double[]) bVar.e();
            for (int i2 = 0; i2 < dArr2.length; i2++) {
                dArr[i + i2] = Double.valueOf(dArr2[i2]);
            }
            return;
        }
        i3.a(bVar.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
        throw null;
    }

    public static void f(R1.c cVar, Integer[] numArr, int i) {
        if (!i3.var_a) {
            int[] iArr = (int[]) cVar.e();
            for (int i2 = 0; i2 < iArr.length; i2++) {
                numArr[i + i2] = Integer.valueOf(iArr[i2]);
            }
            return;
        }
        i3.a(cVar.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
        throw null;
    }

    public static void g(R1.d dVar, Long[] lArr, int i) {
        if (!i3.var_a) {
            long[] jArr = (long[]) dVar.e();
            for (int i2 = 0; i2 < jArr.length; i2++) {
                lArr[i + i2] = Long.valueOf(jArr[i2]);
            }
            return;
        }
        i3.a(dVar.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
        throw null;
    }

    public static void h(R1.b bVar, Consumer consumer) {
        if (consumer instanceof q) {
            bVar.h((q) consumer);
        } else if (!i3.var_a) {
            ((Spliterator.a) bVar.spliterator()).forEachRemaining(consumer);
        } else {
            i3.a(bVar.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void i(R1.c cVar, Consumer consumer) {
        if (consumer instanceof w) {
            cVar.h((w) consumer);
        } else if (!i3.var_a) {
            ((Spliterator.b) cVar.spliterator()).forEachRemaining(consumer);
        } else {
            i3.a(cVar.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void j(R1.d dVar, Consumer consumer) {
        if (consumer instanceof C) {
            dVar.h((C) consumer);
        } else if (!i3.var_a) {
            ((Spliterator.c) dVar.spliterator()).forEachRemaining(consumer);
        } else {
            i3.a(dVar.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static R1.b k(R1.b bVar, long j, long j2, x xVar) {
        if (j == 0 && j2 == bVar.count()) {
            return bVar;
        }
        long j3 = j2 - j;
        Spliterator.a aVar = (Spliterator.a) bVar.spliterator();
        R1.a.CLASSNAMEa j4 = S1.j(j3);
        j4.n(j3);
        for (int i = 0; ((long) i) < j && aVar.o(CLASSNAMEg0.var_a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && aVar.o(j4); i2++) {
        }
        j4.m();
        return j4.a();
    }

    public static R1.c l(R1.c cVar, long j, long j2, x xVar) {
        if (j == 0 && j2 == cVar.count()) {
            return cVar;
        }
        long j3 = j2 - j;
        Spliterator.b bVar = (Spliterator.b) cVar.spliterator();
        R1.a.b p = S1.p(j3);
        p.n(j3);
        for (int i = 0; ((long) i) < j && bVar.h(CLASSNAMEh0.var_a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && bVar.h(p); i2++) {
        }
        p.m();
        return p.a();
    }

    public static R1.d m(R1.d dVar, long j, long j2, x xVar) {
        if (j == 0 && j2 == dVar.count()) {
            return dVar;
        }
        long j3 = j2 - j;
        Spliterator.c cVar = (Spliterator.c) dVar.spliterator();
        R1.a.c q = S1.q(j3);
        q.n(j3);
        for (int i = 0; ((long) i) < j && cVar.j(CLASSNAMEi0.var_a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && cVar.j(q); i2++) {
        }
        q.m();
        return q.a();
    }

    public static R1 n(R1 r1, long j, long j2, x xVar) {
        if (j == 0 && j2 == r1.count()) {
            return r1;
        }
        Spliterator spliterator = r1.spliterator();
        long j3 = j2 - j;
        R1.a d = S1.d(j3, xVar);
        d.n(j3);
        for (int i = 0; ((long) i) < j && spliterator.b(CLASSNAMEf0.var_a); i++) {
        }
        for (int i2 = 0; ((long) i2) < j3 && spliterator.b(d); i2++) {
        }
        d.m();
        return d.a();
    }

    public static CLASSNAMEs1 o(Spliterator.a aVar, boolean z) {
        return new CLASSNAMEp1.g(aVar, T2.c(aVar), z);
    }

    public static C1 p(Spliterator.b bVar, boolean z) {
        return new CLASSNAMEz1.i(bVar, T2.c(bVar), z);
    }

    public static H1 q(Spliterator.c cVar, boolean z) {
        return new D1.g(cVar, T2.c(cVar), z);
    }

    public static g3 r(s sVar, N1 n1) {
        sVar.getClass();
        n1.getClass();
        return new O1(U2.DOUBLE_VALUE, n1, new CLASSNAMEa0(n1, sVar));
    }

    public static g3 s(y yVar, N1 n1) {
        yVar.getClass();
        n1.getClass();
        return new O1(U2.INT_VALUE, n1, new CLASSNAMEc0(n1, yVar));
    }

    public static g3 t(E e, N1 n1) {
        e.getClass();
        n1.getClass();
        return new O1(U2.LONG_VALUE, n1, new CLASSNAMEd0(n1, e));
    }

    public static g3 u(Predicate predicate, N1 n1) {
        predicate.getClass();
        n1.getClass();
        return new O1(U2.REFERENCE, n1, new CLASSNAMEb0(n1, predicate));
    }

    public static Stream v(Spliterator spliterator, boolean z) {
        spliterator.getClass();
        return new CLASSNAMEy2.k(spliterator, T2.c(spliterator), z);
    }
}
