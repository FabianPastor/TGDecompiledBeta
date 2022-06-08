package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.L;
import j$.util.function.BiConsumer;
import j$.util.function.b;
import j$.util.function.m;
import j$.util.function.o;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.t;
import j$.util.function.w;
import j$.util.function.y;
import j$.util.u;
import j$.util.v;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;
import java.util.Iterator;

/* renamed from: j$.util.stream.d1  reason: case insensitive filesystem */
abstract class CLASSNAMEd1 extends CLASSNAMEc implements CLASSNAMEe1 {
    CLASSNAMEd1(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    CLASSNAMEd1(u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    /* access modifiers changed from: private */
    public static v M0(u uVar) {
        if (uVar instanceof v) {
            return (v) uVar;
        }
        if (Q4.a) {
            Q4.a(CLASSNAMEc.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.u r3, j$.util.stream.CLASSNAMEm3 r4) {
        /*
            r2 = this;
            j$.util.v r3 = M0(r3)
            boolean r0 = r4 instanceof j$.util.function.q
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.q r0 = (j$.util.function.q) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.Q4.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.W0 r0 = new j$.util.stream.W0
            r0.<init>((j$.util.stream.CLASSNAMEm3) r4)
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
            j$.util.stream.Q4.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEd1.A0(j$.util.u, j$.util.stream.m3):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEe4 B0() {
        return CLASSNAMEe4.LONG_VALUE;
    }

    public final long D(long j, o oVar) {
        oVar.getClass();
        return ((Long) x0(new P2(CLASSNAMEe4.LONG_VALUE, oVar, j))).longValue();
    }

    /* access modifiers changed from: package-private */
    public final u K0(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        return new s4(y2Var, yVar, z);
    }

    public final boolean L(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEo1.w(j0Var, CLASSNAMEk1.ALL))).booleanValue();
    }

    public final U O(CLASSNAMEl0 l0Var) {
        l0Var.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, l0Var);
    }

    public final Stream Q(r rVar) {
        rVar.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, rVar);
    }

    public final boolean S(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEo1.w(j0Var, CLASSNAMEk1.NONE))).booleanValue();
    }

    public void Z(q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    public final U asDoubleStream() {
        return new O(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n);
    }

    public final CLASSNAMEj average() {
        long[] jArr = (long[]) f0(P0.a, O0.a, R0.a);
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
        return Q(X0.a);
    }

    public final long count() {
        return ((CLASSNAMEd1) z(Y0.a)).sum();
    }

    public void d(q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, false));
    }

    public final CLASSNAMEe1 distinct() {
        return ((CLASSNAMEe3) Q(X0.a)).distinct().g0(Q0.a);
    }

    public final IntStream e0(CLASSNAMEn0 n0Var) {
        n0Var.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, n0Var);
    }

    public final Object f0(y yVar, w wVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 2);
        yVar.getClass();
        wVar.getClass();
        return x0(new CLASSNAMEz2(CLASSNAMEe4.LONG_VALUE, (b) c, wVar, yVar));
    }

    public final CLASSNAMEl findAny() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(false, CLASSNAMEe4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    public final CLASSNAMEl findFirst() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(true, CLASSNAMEe4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    public final CLASSNAMEl g(o oVar) {
        oVar.getClass();
        return (CLASSNAMEl) x0(new D2(CLASSNAMEe4.LONG_VALUE, oVar));
    }

    public final j$.util.r iterator() {
        return L.h(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m573iterator() {
        return L.h(spliterator());
    }

    public final boolean k(CLASSNAMEj0 j0Var) {
        return ((Boolean) x0(CLASSNAMEo1.w(j0Var, CLASSNAMEk1.ANY))).booleanValue();
    }

    public final CLASSNAMEe1 limit(long j) {
        if (j >= 0) {
            return B3.h(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEl max() {
        return g(U0.a);
    }

    public final CLASSNAMEl min() {
        return g(V0.a);
    }

    public final CLASSNAMEe1 p(q qVar) {
        qVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, 0, qVar);
    }

    public final CLASSNAMEe1 s(r rVar) {
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, rVar);
    }

    public final CLASSNAMEe1 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.h(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEe1 sorted() {
        return new L3(this);
    }

    public final v spliterator() {
        return M0(super.spliterator());
    }

    public final long sum() {
        return ((Long) x0(new P2(CLASSNAMEe4.LONG_VALUE, T0.a, 0))).longValue();
    }

    public final CLASSNAMEi summaryStatistics() {
        return (CLASSNAMEi) f0(CLASSNAMEk.a, N0.a, M0.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEs1 t0(long j, m mVar) {
        return CLASSNAMEx2.q(j);
    }

    public final long[] toArray() {
        return (long[]) CLASSNAMEx2.o((CLASSNAMEy1) y0(S0.a)).e();
    }

    public final CLASSNAMEe1 u(CLASSNAMEj0 j0Var) {
        j0Var.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.t, j0Var);
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new G0(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.r);
    }

    public final CLASSNAMEe1 z(t tVar) {
        tVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.LONG_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, tVar);
    }

    /* access modifiers changed from: package-private */
    public final A1 z0(CLASSNAMEy2 y2Var, u uVar, boolean z, m mVar) {
        return CLASSNAMEx2.h(y2Var, uVar, z);
    }
}
