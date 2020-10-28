package j$.util.stream;

import j$.H;
import j$.J;
import j$.N;
import j$.util.C;
import j$.util.CLASSNAMEn;
import j$.util.CLASSNAMEt;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.E;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.v;
import j$.util.x;
import java.util.Iterator;

abstract class K1 extends CLASSNAMEh1 implements L1 {
    K1(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    K1(CLASSNAMEh1 h1Var, int i) {
        super(h1Var, i);
    }

    /* access modifiers changed from: private */
    public static C L0(Spliterator spliterator) {
        if (spliterator instanceof C) {
            return (C) spliterator;
        }
        if (L6.a) {
            L6.a(CLASSNAMEh1.class, "using DoubleStream.adapt(Spliterator<Double> s)");
            throw null;
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEh6 A0() {
        return CLASSNAMEh6.DOUBLE_VALUE;
    }

    public final CLASSNAMEt C(p pVar) {
        pVar.getClass();
        return (CLASSNAMEt) w0(new CLASSNAMEn4(CLASSNAMEh6.DOUBLE_VALUE, pVar));
    }

    public final Object D(E e, B b, BiConsumer biConsumer) {
        CLASSNAMEw wVar = new CLASSNAMEw(biConsumer);
        e.getClass();
        b.getClass();
        return w0(new CLASSNAMEp4(CLASSNAMEh6.DOUBLE_VALUE, wVar, b, e));
    }

    public final double H(double d, p pVar) {
        pVar.getClass();
        return ((Double) w0(new CLASSNAMEl4(CLASSNAMEh6.DOUBLE_VALUE, pVar, d))).doubleValue();
    }

    public final L1 I(N n) {
        n.getClass();
        return new CLASSNAMEt1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, n);
    }

    public final Stream J(r rVar) {
        rVar.getClass();
        return new CLASSNAMEv1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, rVar);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(CLASSNAMEi4 i4Var, E e, boolean z) {
        return new r6(i4Var, e, z);
    }

    public final L1 O(H h) {
        h.getClass();
        return new E1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.y, h);
    }

    public final boolean V(H h) {
        return ((Boolean) w0(CLASSNAMEc3.r(h, Z2.ANY))).booleanValue();
    }

    public final CLASSNAMEt average() {
        double[] dArr = (double[]) D(CLASSNAMEx.a, CLASSNAMEt.a, CLASSNAMEv.a);
        return dArr[2] > 0.0d ? CLASSNAMEt.d(CLASSNAMEo1.a(dArr) / dArr[2]) : CLASSNAMEt.a();
    }

    public final boolean b(H h) {
        return ((Boolean) w0(CLASSNAMEc3.r(h, Z2.NONE))).booleanValue();
    }

    public final Stream boxed() {
        return J(O0.a);
    }

    public final L1 c(q qVar) {
        qVar.getClass();
        return new G1(this, this, CLASSNAMEh6.DOUBLE_VALUE, 0, qVar);
    }

    public final long count() {
        return ((S2) v(CLASSNAMEs.a)).sum();
    }

    public final L1 distinct() {
        return ((CLASSNAMEl5) ((CLASSNAMEl5) J(O0.a)).distinct()).f0(CLASSNAMEn.a);
    }

    public final CLASSNAMEt findAny() {
        return (CLASSNAMEt) w0(new M1(false, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEt.a(), S0.a, U0.a));
    }

    public final CLASSNAMEt findFirst() {
        return (CLASSNAMEt) w0(new M1(true, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEt.a(), S0.a, U0.a));
    }

    public final boolean h0(H h) {
        return ((Boolean) w0(CLASSNAMEc3.r(h, Z2.ALL))).booleanValue();
    }

    public final x iterator() {
        return V.f(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m16iterator() {
        return V.f(spliterator());
    }

    public void k(q qVar) {
        qVar.getClass();
        w0(new T1(qVar, false));
    }

    public void k0(q qVar) {
        qVar.getClass();
        w0(new T1(qVar, true));
    }

    public final L1 limit(long j) {
        if (j >= 0) {
            return D5.f(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEt max() {
        return C(D.a);
    }

    public final CLASSNAMEt min() {
        return C(X0.a);
    }

    public final CLASSNAMEx2 o(J j) {
        j.getClass();
        return new CLASSNAMEx1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, j);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEg3 s0(long j, v vVar) {
        return CLASSNAMEh4.j(j);
    }

    public final L1 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : D5.f(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final L1 sorted() {
        return new L5(this);
    }

    public final C spliterator() {
        return L0(super.spliterator());
    }

    public final double sum() {
        return CLASSNAMEo1.a((double[]) D(CLASSNAMEu.a, r.a, CLASSNAMEp.a));
    }

    public final CLASSNAMEn summaryStatistics() {
        return (CLASSNAMEn) D(I0.a, Z.a, CLASSNAMEm0.a);
    }

    public final double[] toArray() {
        return (double[]) CLASSNAMEh4.m((CLASSNAMEh3) x0(CLASSNAMEq.a)).e();
    }

    public final L1 u(r rVar) {
        return new B1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, rVar);
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new C1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.w);
    }

    public final T2 v(s sVar) {
        sVar.getClass();
        return new CLASSNAMEz1(this, this, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, sVar);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 y0(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar) {
        return CLASSNAMEh4.f(i4Var, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.CLASSNAMEt5 r4) {
        /*
            r2 = this;
            j$.util.C r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.q
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.q r0 = (j$.util.function.q) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.L6.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.N r0 = new j$.util.stream.N
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.p()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.o(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using DoubleStream.adapt(Sink<Double> s)"
            j$.util.stream.L6.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.K1.z0(j$.util.Spliterator, j$.util.stream.t5):void");
    }
}
