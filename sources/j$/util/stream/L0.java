package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEp;
import j$.util.N;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEb;
import j$.util.function.j;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.util.function.z;
import j$.util.v;
import j$.util.y;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;
import java.util.Iterator;

abstract class L0 extends CLASSNAMEc implements M0 {
    L0(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    L0(y yVar, int i, boolean z) {
        super(yVar, i, z);
    }

    /* access modifiers changed from: private */
    public static v M0(y yVar) {
        if (yVar instanceof v) {
            return (v) yVar;
        }
        if (R4.a) {
            R4.a(CLASSNAMEc.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    public final U A(X x) {
        x.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, x);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.y r3, j$.util.stream.CLASSNAMEn3 r4) {
        /*
            r2 = this;
            j$.util.v r3 = M0(r3)
            boolean r0 = r4 instanceof j$.util.function.l
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.l r0 = (j$.util.function.l) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.R4.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.B0 r0 = new j$.util.stream.B0
            r0.<init>((j$.util.stream.CLASSNAMEn3) r4)
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
            j$.util.stream.R4.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.L0.A0(j$.util.y, j$.util.stream.n3):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEf4 B0() {
        return CLASSNAMEf4.INT_VALUE;
    }

    public final boolean C(V v) {
        return ((Boolean) x0(CLASSNAMEp1.v(v, CLASSNAMEl1.ALL))).booleanValue();
    }

    public final boolean F(V v) {
        return ((Boolean) x0(CLASSNAMEp1.v(v, CLASSNAMEl1.ANY))).booleanValue();
    }

    public void I(l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, true));
    }

    public final Stream J(m mVar) {
        mVar.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, mVar);
    }

    /* access modifiers changed from: package-private */
    public final y K0(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        return new CLASSNAMEr4(z2Var, zVar, z);
    }

    public final int N(int i, j jVar) {
        jVar.getClass();
        return ((Integer) x0(new M2(CLASSNAMEf4.INT_VALUE, jVar, i))).intValue();
    }

    public final M0 P(m mVar) {
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, mVar);
    }

    public void U(l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, false));
    }

    public final CLASSNAMEk a0(j jVar) {
        jVar.getClass();
        return (CLASSNAMEk) x0(new E2(CLASSNAMEf4.INT_VALUE, jVar));
    }

    public final U asDoubleStream() {
        return new O(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n);
    }

    public final CLASSNAMEf1 asLongStream() {
        return new G0(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n);
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

    public final M0 c0(l lVar) {
        lVar.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, 0, lVar);
    }

    public final long count() {
        return ((CLASSNAMEe1) f(E0.a)).sum();
    }

    public final M0 distinct() {
        return ((CLASSNAMEf3) J(C0.a)).distinct().m(CLASSNAMEw0.a);
    }

    public final CLASSNAMEf1 f(n nVar) {
        nVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, nVar);
    }

    public final CLASSNAMEk findAny() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(false, CLASSNAMEf4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    public final CLASSNAMEk findFirst() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(true, CLASSNAMEf4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    public final M0 h(V v) {
        v.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.t, v);
    }

    public final CLASSNAMEp iterator() {
        return N.g(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m1186iterator() {
        return N.g(spliterator());
    }

    public final Object k0(z zVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        zVar.getClass();
        vVar.getClass();
        return x0(new A2(CLASSNAMEf4.INT_VALUE, (CLASSNAMEb) c, vVar, zVar));
    }

    public final M0 limit(long j) {
        if (j >= 0) {
            return C3.g(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEk max() {
        return a0(CLASSNAMEz0.a);
    }

    public final CLASSNAMEk min() {
        return a0(A0.a);
    }

    public final M0 q(CLASSNAMEb0 b0Var) {
        b0Var.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, b0Var);
    }

    public final M0 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : C3.g(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final M0 sorted() {
        return new L3(this);
    }

    public final v spliterator() {
        return M0(super.spliterator());
    }

    public final int sum() {
        return ((Integer) x0(new M2(CLASSNAMEf4.INT_VALUE, CLASSNAMEy0.a, 0))).intValue();
    }

    public final CLASSNAMEh summaryStatistics() {
        return (CLASSNAMEh) k0(CLASSNAMEj.a, CLASSNAMEt0.a, CLASSNAMEs0.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt1 t0(long j, m mVar) {
        return CLASSNAMEy2.p(j);
    }

    public final int[] toArray() {
        return (int[]) CLASSNAMEy2.n((CLASSNAMEx1) y0(D0.a)).e();
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new H0(this, this, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.r);
    }

    public final boolean v(V v) {
        return ((Boolean) x0(CLASSNAMEp1.v(v, CLASSNAMEl1.NONE))).booleanValue();
    }

    /* access modifiers changed from: package-private */
    public final B1 z0(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar) {
        return CLASSNAMEy2.g(z2Var, yVar, z);
    }
}
