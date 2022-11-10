package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.u;
import j$.wrappers.CLASSNAMEj0;
/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract /* synthetic */ class AbstractCLASSNAMEo1 {
    public static void a(InterfaceCLASSNAMEj3 interfaceCLASSNAMEj3, Double d) {
        if (!Q4.a) {
            interfaceCLASSNAMEj3.accept(d.doubleValue());
        } else {
            Q4.a(interfaceCLASSNAMEj3.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(InterfaceCLASSNAMEk3 interfaceCLASSNAMEk3, Integer num) {
        if (!Q4.a) {
            interfaceCLASSNAMEk3.accept(num.intValue());
        } else {
            Q4.a(interfaceCLASSNAMEk3.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(InterfaceCLASSNAMEl3 interfaceCLASSNAMEl3, Long l) {
        if (!Q4.a) {
            interfaceCLASSNAMEl3.accept(l.longValue());
        } else {
            Q4.a(interfaceCLASSNAMEl3.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1, j$.util.function.m mVar) {
        if (Q4.a) {
            Q4.a(interfaceCLASSNAMEz1.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (interfaceCLASSNAMEz1.count() >= NUM) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        } else {
            Object[] objArr = (Object[]) mVar.apply((int) interfaceCLASSNAMEz1.count());
            interfaceCLASSNAMEz1.i(objArr, 0);
            return objArr;
        }
    }

    public static void h(InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1, Double[] dArr, int i) {
        if (Q4.a) {
            Q4.a(interfaceCLASSNAMEu1.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
            throw null;
        }
        double[] dArr2 = (double[]) interfaceCLASSNAMEu1.e();
        for (int i2 = 0; i2 < dArr2.length; i2++) {
            dArr[i + i2] = Double.valueOf(dArr2[i2]);
        }
    }

    public static void i(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1, Integer[] numArr, int i) {
        if (Q4.a) {
            Q4.a(interfaceCLASSNAMEw1.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            throw null;
        }
        int[] iArr = (int[]) interfaceCLASSNAMEw1.e();
        for (int i2 = 0; i2 < iArr.length; i2++) {
            numArr[i + i2] = Integer.valueOf(iArr[i2]);
        }
    }

    public static void j(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1, Long[] lArr, int i) {
        if (Q4.a) {
            Q4.a(interfaceCLASSNAMEy1.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
            throw null;
        }
        long[] jArr = (long[]) interfaceCLASSNAMEy1.e();
        for (int i2 = 0; i2 < jArr.length; i2++) {
            lArr[i + i2] = Long.valueOf(jArr[i2]);
        }
    }

    public static void k(InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            interfaceCLASSNAMEu1.g((j$.util.function.f) consumer);
        } else if (!Q4.a) {
            ((j$.util.t) interfaceCLASSNAMEu1.moNUMspliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(interfaceCLASSNAMEu1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            interfaceCLASSNAMEw1.g((j$.util.function.l) consumer);
        } else if (!Q4.a) {
            ((u.a) interfaceCLASSNAMEw1.moNUMspliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(interfaceCLASSNAMEw1.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            interfaceCLASSNAMEy1.g((j$.util.function.q) consumer);
        } else if (!Q4.a) {
            ((j$.util.v) interfaceCLASSNAMEy1.moNUMspliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(interfaceCLASSNAMEy1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static InterfaceCLASSNAMEu1 n(InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == interfaceCLASSNAMEu1.count()) {
            return interfaceCLASSNAMEu1;
        }
        long j3 = j2 - j;
        j$.util.t tVar = (j$.util.t) interfaceCLASSNAMEu1.moNUMspliterator();
        InterfaceCLASSNAMEp1 j4 = AbstractCLASSNAMEx2.j(j3);
        j4.n(j3);
        for (int i = 0; i < j && tVar.k(CLASSNAMEt1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && tVar.k(j4); i2++) {
        }
        j4.m();
        return j4.moNUMa();
    }

    public static InterfaceCLASSNAMEw1 o(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == interfaceCLASSNAMEw1.count()) {
            return interfaceCLASSNAMEw1;
        }
        long j3 = j2 - j;
        u.a aVar = (u.a) interfaceCLASSNAMEw1.moNUMspliterator();
        InterfaceCLASSNAMEq1 p = AbstractCLASSNAMEx2.p(j3);
        p.n(j3);
        for (int i = 0; i < j && aVar.g(CLASSNAMEv1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && aVar.g(p); i2++) {
        }
        p.m();
        return p.moNUMa();
    }

    public static InterfaceCLASSNAMEy1 p(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == interfaceCLASSNAMEy1.count()) {
            return interfaceCLASSNAMEy1;
        }
        long j3 = j2 - j;
        j$.util.v vVar = (j$.util.v) interfaceCLASSNAMEy1.moNUMspliterator();
        InterfaceCLASSNAMEr1 q = AbstractCLASSNAMEx2.q(j3);
        q.n(j3);
        for (int i = 0; i < j && vVar.i(CLASSNAMEx1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && vVar.i(q); i2++) {
        }
        q.m();
        return q.moNUMa();
    }

    public static A1 q(A1 a1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == a1.count()) {
            return a1;
        }
        j$.util.u moNUMspliterator = a1.moNUMspliterator();
        long j3 = j2 - j;
        InterfaceCLASSNAMEs1 d = AbstractCLASSNAMEx2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; i < j && moNUMspliterator.b(CLASSNAMEn1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && moNUMspliterator.b(d); i2++) {
        }
        d.m();
        return d.moNUMa();
    }

    public static U r(j$.util.t tVar, boolean z) {
        return new P(tVar, EnumCLASSNAMEd4.c(tVar), z);
    }

    public static IntStream s(u.a aVar, boolean z) {
        return new I0(aVar, EnumCLASSNAMEd4.c(aVar), z);
    }

    public static InterfaceCLASSNAMEe1 t(j$.util.v vVar, boolean z) {
        return new CLASSNAMEa1(vVar, EnumCLASSNAMEd4.c(vVar), z);
    }

    public static N4 u(j$.wrappers.E e, EnumCLASSNAMEk1 enumCLASSNAMEk1) {
        e.getClass();
        enumCLASSNAMEk1.getClass();
        return new CLASSNAMEl1(EnumCLASSNAMEe4.DOUBLE_VALUE, enumCLASSNAMEk1, new CLASSNAMEo(enumCLASSNAMEk1, e));
    }

    public static N4 v(j$.wrappers.V v, EnumCLASSNAMEk1 enumCLASSNAMEk1) {
        v.getClass();
        enumCLASSNAMEk1.getClass();
        return new CLASSNAMEl1(EnumCLASSNAMEe4.INT_VALUE, enumCLASSNAMEk1, new CLASSNAMEo(enumCLASSNAMEk1, v));
    }

    public static N4 w(CLASSNAMEj0 CLASSNAMEj0, EnumCLASSNAMEk1 enumCLASSNAMEk1) {
        CLASSNAMEj0.getClass();
        enumCLASSNAMEk1.getClass();
        return new CLASSNAMEl1(EnumCLASSNAMEe4.LONG_VALUE, enumCLASSNAMEk1, new CLASSNAMEo(enumCLASSNAMEk1, CLASSNAMEj0));
    }

    public static N4 x(Predicate predicate, EnumCLASSNAMEk1 enumCLASSNAMEk1) {
        predicate.getClass();
        enumCLASSNAMEk1.getClass();
        return new CLASSNAMEl1(EnumCLASSNAMEe4.REFERENCE, enumCLASSNAMEk1, new CLASSNAMEo(enumCLASSNAMEk1, predicate));
    }

    public static Stream y(j$.util.u uVar, boolean z) {
        uVar.getClass();
        return new CLASSNAMEb3(uVar, EnumCLASSNAMEd4.c(uVar), z);
    }
}
