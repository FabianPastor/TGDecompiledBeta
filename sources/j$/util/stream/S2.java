package j$.util.stream;

import j$.CLASSNAMEm0;
import j$.CLASSNAMEo0;
import j$.CLASSNAMEq0;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEv;
import j$.util.E;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.D;
import j$.util.function.v;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.r;
import java.util.Iterator;

abstract class S2 extends CLASSNAMEh1 implements T2 {
    S2(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    S2(CLASSNAMEh1 h1Var, int i) {
        super(h1Var, i);
    }

    /* access modifiers changed from: private */
    public static E L0(Spliterator spliterator) {
        if (spliterator instanceof E) {
            return (E) spliterator;
        }
        if (L6.a) {
            L6.a(CLASSNAMEh1.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    public final long A(long j, x xVar) {
        xVar.getClass();
        return ((Long) w0(new F4(CLASSNAMEh6.LONG_VALUE, xVar, j))).longValue();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEh6 A0() {
        return CLASSNAMEh6.LONG_VALUE;
    }

    public final T2 G(CLASSNAMEm0 m0Var) {
        m0Var.getClass();
        return new M2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.y, m0Var);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(CLASSNAMEi4 i4Var, j$.util.function.E e, boolean z) {
        return new t6(i4Var, e, z);
    }

    public final Stream N(z zVar) {
        zVar.getClass();
        return new D2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, zVar);
    }

    public void X(y yVar) {
        yVar.getClass();
        w0(new V1(yVar, true));
    }

    public final L1 asDoubleStream() {
        return new CLASSNAMEz2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s);
    }

    public final CLASSNAMEt average() {
        long[] jArr = (long[]) c0(W.a, P.a, O.a);
        if (jArr[0] <= 0) {
            return CLASSNAMEt.a();
        }
        double d = (double) jArr[1];
        double d2 = (double) jArr[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return CLASSNAMEt.d(d / d2);
    }

    public final Stream boxed() {
        return N(CLASSNAMEa.a);
    }

    public final Object c0(j$.util.function.E e, D d, BiConsumer biConsumer) {
        T t = new T(biConsumer);
        e.getClass();
        d.getClass();
        return w0(new CLASSNAMEj4(CLASSNAMEh6.LONG_VALUE, t, d, e));
    }

    public final long count() {
        return ((S2) x(V.a)).sum();
    }

    public final T2 distinct() {
        return ((CLASSNAMEl5) ((CLASSNAMEl5) N(CLASSNAMEa.a)).distinct()).d0(U.a);
    }

    public void e(y yVar) {
        yVar.getClass();
        w0(new V1(yVar, false));
    }

    public final CLASSNAMEv findAny() {
        return (CLASSNAMEv) w0(new M1(false, CLASSNAMEh6.LONG_VALUE, CLASSNAMEv.a(), Z0.a, CLASSNAMEg0.a));
    }

    public final CLASSNAMEv findFirst() {
        return (CLASSNAMEv) w0(new M1(true, CLASSNAMEh6.LONG_VALUE, CLASSNAMEv.a(), Z0.a, CLASSNAMEg0.a));
    }

    public final CLASSNAMEv h(x xVar) {
        xVar.getClass();
        return (CLASSNAMEv) w0(new H4(CLASSNAMEh6.LONG_VALUE, xVar));
    }

    public final L1 i(CLASSNAMEo0 o0Var) {
        o0Var.getClass();
        return new H2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, o0Var);
    }

    public final j$.util.z iterator() {
        return V.h(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m19iterator() {
        return V.h(spliterator());
    }

    public final boolean l(CLASSNAMEm0 m0Var) {
        return ((Boolean) w0(CLASSNAMEc3.t(m0Var, Z2.ANY))).booleanValue();
    }

    public final T2 limit(long j) {
        if (j >= 0) {
            return D5.h(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEv max() {
        return h(Y0.a);
    }

    public final CLASSNAMEv min() {
        return h(CLASSNAMEa0.a);
    }

    public final T2 q(y yVar) {
        yVar.getClass();
        return new O2(this, this, CLASSNAMEh6.LONG_VALUE, 0, yVar);
    }

    public final boolean r(CLASSNAMEm0 m0Var) {
        return ((Boolean) w0(CLASSNAMEc3.t(m0Var, Z2.NONE))).booleanValue();
    }

    public final T2 s(z zVar) {
        return new J2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, zVar);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEg3 s0(long j, v vVar) {
        return CLASSNAMEh4.q(j);
    }

    public final T2 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : D5.h(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final T2 sorted() {
        return new N5(this);
    }

    public final E spliterator() {
        return L0(super.spliterator());
    }

    public final long sum() {
        return ((Long) w0(new F4(CLASSNAMEh6.LONG_VALUE, X.a, 0))).longValue();
    }

    public final r summaryStatistics() {
        return (r) c0(CLASSNAMEf1.a, CLASSNAMEo0.a, CLASSNAMEx0.a);
    }

    public final long[] toArray() {
        return (long[]) CLASSNAMEh4.o((CLASSNAMEj3) x0(S.a)).e();
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new K2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.w);
    }

    public final CLASSNAMEx2 w(CLASSNAMEq0 q0Var) {
        q0Var.getClass();
        return new F2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, q0Var);
    }

    public final T2 x(A a) {
        a.getClass();
        return new B2(this, this, CLASSNAMEh6.LONG_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, a);
    }

    public final boolean y(CLASSNAMEm0 m0Var) {
        return ((Boolean) w0(CLASSNAMEc3.t(m0Var, Z2.ALL))).booleanValue();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 y0(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar) {
        return CLASSNAMEh4.h(i4Var, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.CLASSNAMEt5 r4) {
        /*
            r2 = this;
            j$.util.E r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.y
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.y r0 = (j$.util.function.y) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.L6.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.K0 r0 = new j$.util.stream.K0
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.p()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.j(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using LongStream.adapt(Sink<Long> s)"
            j$.util.stream.L6.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.S2.z0(j$.util.Spliterator, j$.util.stream.t5):void");
    }
}
