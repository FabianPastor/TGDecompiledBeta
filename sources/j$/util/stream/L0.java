package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.L;
import j$.util.function.BiConsumer;
import j$.util.function.b;
import j$.util.function.j;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.util.function.v;
import j$.util.function.y;
import j$.util.p;
import j$.util.u;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;
import java.util.Iterator;

abstract class L0 extends CLASSNAMEc implements IntStream {
    L0(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    L0(u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    /* access modifiers changed from: private */
    public static u.a M0(u uVar) {
        if (uVar instanceof u.a) {
            return (u.a) uVar;
        }
        if (Q4.a) {
            Q4.a(CLASSNAMEc.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    public final U A(X x) {
        x.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, x);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.u r3, j$.util.stream.CLASSNAMEm3 r4) {
        /*
            r2 = this;
            j$.util.u$a r3 = M0(r3)
            boolean r0 = r4 instanceof j$.util.function.l
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.l r0 = (j$.util.function.l) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.Q4.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.B0 r0 = new j$.util.stream.B0
            r0.<init>((j$.util.stream.CLASSNAMEm3) r4)
        L_0x0015:
            boolean r1 = r4.o()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.g(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.c> r3 = j$.util.stream.CLASSNAMEc.class
            java.lang.String r4 = "using IntStream.adapt(Sink<Integer> s)"
            j$.util.stream.Q4.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.L0.A0(j$.util.u, j$.util.stream.m3):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEe4 B0() {
        return CLASSNAMEe4.INT_VALUE;
    }

    public final boolean C(V v) {
        return ((Boolean) x0(CLASSNAMEo1.v(v, CLASSNAMEk1.ALL))).booleanValue();
    }

    public final boolean F(V v) {
        return ((Boolean) x0(CLASSNAMEo1.v(v, CLASSNAMEk1.ANY))).booleanValue();
    }

    public void I(l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, true));
    }

    public final Stream J(m mVar) {
        mVar.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, mVar);
    }

    /* access modifiers changed from: package-private */
    public final u K0(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        return new CLASSNAMEq4(y2Var, yVar, z);
    }

    public final int N(int i, j jVar) {
        jVar.getClass();
        return ((Integer) x0(new L2(CLASSNAMEe4.INT_VALUE, jVar, i))).intValue();
    }

    public final IntStream P(m mVar) {
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, mVar);
    }

    public void U(l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, false));
    }

    public final CLASSNAMEk a0(j jVar) {
        jVar.getClass();
        return (CLASSNAMEk) x0(new D2(CLASSNAMEe4.INT_VALUE, jVar));
    }

    public final U asDoubleStream() {
        return new O(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n);
    }

    public final CLASSNAMEe1 asLongStream() {
        return new G0(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n);
    }

    public final CLASSNAMEj average() {
        long[] jArr = (long[]) k0(CLASSNAMEv0.a, CLASSNAMEu0.a, CLASSNAMEx0.a);
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
        return J(C0.a);
    }

    public final IntStream c0(l lVar) {
        lVar.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, 0, lVar);
    }

    public final long count() {
        return ((CLASSNAMEd1) f(E0.a)).sum();
    }

    public final IntStream distinct() {
        return ((CLASSNAMEe3) J(C0.a)).distinct().m(CLASSNAMEw0.a);
    }

    public final CLASSNAMEe1 f(n nVar) {
        nVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, nVar);
    }

    public final CLASSNAMEk findAny() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(false, CLASSNAMEe4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    public final CLASSNAMEk findFirst() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(true, CLASSNAMEe4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    public final IntStream h(V v) {
        v.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.t, v);
    }

    public final p.a iterator() {
        return L.g(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m0iterator() {
        return L.g(spliterator());
    }

    public final Object k0(y yVar, v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        yVar.getClass();
        vVar.getClass();
        return x0(new CLASSNAMEz2(CLASSNAMEe4.INT_VALUE, (b) c, vVar, yVar));
    }

    public final IntStream limit(long j) {
        if (j >= 0) {
            return B3.g(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEk max() {
        return a0(CLASSNAMEz0.a);
    }

    public final CLASSNAMEk min() {
        return a0(A0.a);
    }

    public final IntStream q(CLASSNAMEb0 b0Var) {
        b0Var.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.p | CLASSNAMEd4.n, b0Var);
    }

    public final IntStream skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.g(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final IntStream sorted() {
        return new K3(this);
    }

    public final u.a spliterator() {
        return M0(super.spliterator());
    }

    public final int sum() {
        return ((Integer) x0(new L2(CLASSNAMEe4.INT_VALUE, CLASSNAMEy0.a, 0))).intValue();
    }

    public final CLASSNAMEh summaryStatistics() {
        return (CLASSNAMEh) k0(CLASSNAMEj.a, CLASSNAMEt0.a, CLASSNAMEs0.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEs1 t0(long j, m mVar) {
        return CLASSNAMEx2.p(j);
    }

    public final int[] toArray() {
        return (int[]) CLASSNAMEx2.n((CLASSNAMEw1) y0(D0.a)).e();
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new H0(this, this, CLASSNAMEe4.INT_VALUE, CLASSNAMEd4.r);
    }

    public final boolean v(V v) {
        return ((Boolean) x0(CLASSNAMEo1.v(v, CLASSNAMEk1.NONE))).booleanValue();
    }

    /* access modifiers changed from: package-private */
    public final A1 z0(CLASSNAMEy2 y2Var, u uVar, boolean z, m mVar) {
        return CLASSNAMEx2.g(y2Var, uVar, z);
    }
}
