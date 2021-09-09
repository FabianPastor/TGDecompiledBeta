package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEn;
import j$.util.N;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEb;
import j$.util.function.d;
import j$.util.function.f;
import j$.util.function.g;
import j$.util.function.h;
import j$.util.function.m;
import j$.util.function.z;
import j$.util.u;
import j$.util.y;
import j$.wrappers.E;
import j$.wrappers.G;
import j$.wrappers.K;
import java.util.Iterator;

abstract class T extends CLASSNAMEc implements U {
    T(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    T(y yVar, int i, boolean z) {
        super(yVar, i, z);
    }

    /* access modifiers changed from: private */
    public static u M0(y yVar) {
        if (yVar instanceof u) {
            return (u) yVar;
        }
        if (R4.a) {
            R4.a(CLASSNAMEc.class, "using DoubleStream.adapt(Spliterator<Double> s)");
            throw null;
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.y r3, j$.util.stream.CLASSNAMEn3 r4) {
        /*
            r2 = this;
            j$.util.u r3 = M0(r3)
            boolean r0 = r4 instanceof j$.util.function.f
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.f r0 = (j$.util.function.f) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.R4.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.F r0 = new j$.util.stream.F
            r0.<init>((j$.util.stream.CLASSNAMEn3) r4)
        L_0x0015:
            boolean r1 = r4.o()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.k(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.c> r3 = j$.util.stream.CLASSNAMEc.class
            java.lang.String r4 = "using DoubleStream.adapt(Sink<Double> s)"
            j$.util.stream.R4.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.T.A0(j$.util.y, j$.util.stream.n3):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEf4 B0() {
        return CLASSNAMEf4.DOUBLE_VALUE;
    }

    public final CLASSNAMEj G(d dVar) {
        dVar.getClass();
        return (CLASSNAMEj) x0(new E2(CLASSNAMEf4.DOUBLE_VALUE, dVar));
    }

    public final Object H(z zVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 0);
        zVar.getClass();
        uVar.getClass();
        return x0(new A2(CLASSNAMEf4.DOUBLE_VALUE, (CLASSNAMEb) c, uVar, zVar));
    }

    public final double K(double d, d dVar) {
        dVar.getClass();
        return ((Double) x0(new C2(CLASSNAMEf4.DOUBLE_VALUE, dVar, d))).doubleValue();
    }

    /* access modifiers changed from: package-private */
    public final y K0(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        return new CLASSNAMEp4(z2Var, zVar, z);
    }

    public final Stream M(g gVar) {
        gVar.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, gVar);
    }

    public final M0 R(G g) {
        g.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, g);
    }

    public final boolean Y(E e) {
        return ((Boolean) x0(CLASSNAMEp1.u(e, CLASSNAMEl1.ALL))).booleanValue();
    }

    public final CLASSNAMEj average() {
        double[] dArr = (double[]) H(CLASSNAMEx.a, CLASSNAMEv.a, A.a);
        return dArr[2] > 0.0d ? CLASSNAMEj.d(CLASSNAMEl.a(dArr) / dArr[2]) : CLASSNAMEj.a();
    }

    public final U b(f fVar) {
        fVar.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, 0, fVar);
    }

    public final Stream boxed() {
        return M(G.a);
    }

    public final long count() {
        return ((CLASSNAMEe1) x(H.a)).sum();
    }

    public final U distinct() {
        return ((CLASSNAMEf3) M(G.a)).distinct().j0(CLASSNAMEz.a);
    }

    public final CLASSNAMEj findAny() {
        return (CLASSNAMEj) x0(new CLASSNAMEd0(false, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEj.a(), W.a, Z.a));
    }

    public final CLASSNAMEj findFirst() {
        return (CLASSNAMEj) x0(new CLASSNAMEd0(true, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEj.a(), W.a, Z.a));
    }

    public final boolean h0(E e) {
        return ((Boolean) x0(CLASSNAMEp1.u(e, CLASSNAMEl1.ANY))).booleanValue();
    }

    public final boolean i0(E e) {
        return ((Boolean) x0(CLASSNAMEp1.u(e, CLASSNAMEl1.NONE))).booleanValue();
    }

    public final CLASSNAMEn iterator() {
        return N.f(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m4iterator() {
        return N.f(spliterator());
    }

    public void j(f fVar) {
        fVar.getClass();
        x0(new CLASSNAMEk0(fVar, false));
    }

    public void l0(f fVar) {
        fVar.getClass();
        x0(new CLASSNAMEk0(fVar, true));
    }

    public final U limit(long j) {
        if (j >= 0) {
            return C3.f(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEj max() {
        return G(D.a);
    }

    public final CLASSNAMEj min() {
        return G(E.a);
    }

    public final U r(E e) {
        e.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.t, e);
    }

    public final U skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : C3.f(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final U sorted() {
        return new K3(this);
    }

    public final u spliterator() {
        return M0(super.spliterator());
    }

    public final double sum() {
        return CLASSNAMEl.a((double[]) H(CLASSNAMEy.a, CLASSNAMEw.a, B.a));
    }

    public final CLASSNAMEg summaryStatistics() {
        return (CLASSNAMEg) H(CLASSNAMEi.a, CLASSNAMEu.a, CLASSNAMEt.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt1 t0(long j, m mVar) {
        return CLASSNAMEy2.j(j);
    }

    public final double[] toArray() {
        return (double[]) CLASSNAMEy2.m((CLASSNAMEv1) y0(I.a)).e();
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new O(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.r);
    }

    public final U w(g gVar) {
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, gVar);
    }

    public final CLASSNAMEf1 x(h hVar) {
        hVar.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, hVar);
    }

    public final U y(K k) {
        k.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.p | CLASSNAMEe4.n, k);
    }

    /* access modifiers changed from: package-private */
    public final B1 z0(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar) {
        return CLASSNAMEy2.f(z2Var, yVar, z);
    }
}
