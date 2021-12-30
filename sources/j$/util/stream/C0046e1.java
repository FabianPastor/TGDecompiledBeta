package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.N;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEb;
import j$.util.function.m;
import j$.util.function.o;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.t;
import j$.util.w;
import j$.util.y;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;
import java.util.Iterator;

/* renamed from: j$.util.stream.e1  reason: case insensitive filesystem */
abstract class CLASSNAMEe1 extends CLASSNAMEc implements CLASSNAMEf1 {
    CLASSNAMEe1(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    CLASSNAMEe1(y yVar, int i, boolean z) {
        super(yVar, i, z);
    }

    /* access modifiers changed from: private */
    public static w M0(y yVar) {
        if (yVar instanceof w) {
            return (w) yVar;
        }
        if (R4.a) {
            R4.a(CLASSNAMEc.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.y r3, j$.util.stream.CLASSNAMEn3 r4) {
        /*
            r2 = this;
            j$.util.w r3 = M0(r3)
            boolean r0 = r4 instanceof j$.util.function.q
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.q r0 = (j$.util.function.q) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.R4.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.X0 r0 = new j$.util.stream.X0
            r0.<init>((j$.util.stream.CLASSNAMEn3) r4)
        L_0x0015:
            boolean r1 = r4.o()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.i(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.c> r3 = j$.util.stream.CLASSNAMEc.class
            java.lang.String r4 = "using LongStream.adapt(Sink<Long> s)"
            j$.util.stream.R4.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEe1.A0(j$.util.y, j$.util.stream.n3):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEf4 B0() {
        return CLASSNAMEf4.LONG_VALUE;
    }

    public final long D(long j, o oVar) {
        oVar.getClass();
        return ((Long) x0(new Q2(CLASSNAMEf4.LONG_VALUE, oVar, j))).longValue();
    }

    /* access modifiers changed from: package-private */
    public final y K0(CLASSNAMEz2 z2Var, j$.util.function.y yVar, boolean z) {
        return new t4(z2Var, yVar, z);
    }

    public final boolean L(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEp1.w(j0Var, CLASSNAMEl1.ALL))).booleanValue();
    }

    public final U O(CLASSNAMEl0 l0Var) {
        l0Var.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, l0Var);
    }

    public final Stream Q(r rVar) {
        rVar.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, rVar);
    }

    public final boolean S(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEp1.w(j0Var, CLASSNAMEl1.NONE))).booleanValue();
    }

    public void Z(q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    public final U asDoubleStream() {
        return new O(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n);
    }

    public final CLASSNAMEj average() {
        long[] jArr = (long[]) f0(Q0.a, P0.a, S0.a);
        if (jArr[0] <= 0) {
            return CLASSNAMEj.a();
        }
        double d = (double) jArr[1];
        double d2 = (double) jArr[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return CLASSNAMEj.d(d / d2);
    }

    public final Stream boxed() {
        return Q(Y0.a);
    }

    public final long count() {
        return ((CLASSNAMEe1) z(Z0.a)).sum();
    }

    public void d(q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, false));
    }

    public final CLASSNAMEf1 distinct() {
        return ((CLASSNAMEf3) Q(Y0.a)).distinct().g0(R0.a);
    }

    public final M0 e0(CLASSNAMEn0 n0Var) {
        n0Var.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, n0Var);
    }

    public final Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 2);
        yVar.getClass();
        wVar.getClass();
        return x0(new A2(CLASSNAMEf4.LONG_VALUE, (CLASSNAMEb) c, wVar, yVar));
    }

    public final CLASSNAMEl findAny() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(false, CLASSNAMEf4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    public final CLASSNAMEl findFirst() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(true, CLASSNAMEf4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    public final CLASSNAMEl g(o oVar) {
        oVar.getClass();
        return (CLASSNAMEl) x0(new E2(CLASSNAMEf4.LONG_VALUE, oVar));
    }

    public final j$.util.r iterator() {
        return N.h(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m537iterator() {
        return N.h(spliterator());
    }

    public final boolean k(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEp1.w(j0Var, CLASSNAMEl1.ANY))).booleanValue();
    }

    public final CLASSNAMEf1 limit(long j) {
        if (j >= 0) {
            return C3.h(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEl max() {
        return g(V0.a);
    }

    public final CLASSNAMEl min() {
        return g(W0.a);
    }

    public final CLASSNAMEf1 p(q qVar) {
        qVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, 0, qVar);
    }

    public final CLASSNAMEf1 s(r rVar) {
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, rVar);
    }

    public final CLASSNAMEf1 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : C3.h(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEf1 sorted() {
        return new M3(this);
    }

    public final w spliterator() {
        return M0(super.spliterator());
    }

    public final long sum() {
        return ((Long) x0(new Q2(CLASSNAMEf4.LONG_VALUE, U0.a, 0))).longValue();
    }

    public final CLASSNAMEi summaryStatistics() {
        return (CLASSNAMEi) f0(CLASSNAMEk.a, O0.a, N0.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt1 t0(long j, m mVar) {
        return CLASSNAMEy2.q(j);
    }

    public final long[] toArray() {
        return (long[]) CLASSNAMEy2.o((CLASSNAMEz1) y0(T0.a)).e();
    }

    public final CLASSNAMEf1 u(CLASSNAMEj0 j0Var) {
        j0Var.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.t, j0Var);
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new G0(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.r);
    }

    public final CLASSNAMEf1 z(t tVar) {
        tVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, tVar);
    }

    /* access modifiers changed from: package-private */
    public final B1 z0(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar) {
        return CLASSNAMEy2.h(z2Var, yVar, z);
    }
}
