package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.CLASSNAMEb0;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class L0 extends AbstractCLASSNAMEc implements IntStream {
    public L0(AbstractCLASSNAMEc abstractCLASSNAMEc, int i) {
        super(abstractCLASSNAMEc, i);
    }

    public L0(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ u.a L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static u.a M0(j$.util.u uVar) {
        if (uVar instanceof u.a) {
            return (u.a) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
        }
        Q4.a(AbstractCLASSNAMEc.class, "using IntStream.adapt(Spliterator<Integer> s)");
        throw null;
    }

    @Override // j$.util.stream.IntStream
    public final U A(j$.wrappers.X x) {
        x.getClass();
        return new K(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, x);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final void A0(j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        j$.util.function.l b0;
        u.a M0 = M0(uVar);
        if (interfaceCLASSNAMEm3 instanceof j$.util.function.l) {
            b0 = (j$.util.function.l) interfaceCLASSNAMEm3;
        } else if (Q4.a) {
            Q4.a(AbstractCLASSNAMEc.class, "using IntStream.adapt(Sink<Integer> s)");
            throw null;
        } else {
            b0 = new B0(interfaceCLASSNAMEm3);
        }
        while (!interfaceCLASSNAMEm3.o() && M0.g(b0)) {
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final EnumCLASSNAMEe4 B0() {
        return EnumCLASSNAMEe4.INT_VALUE;
    }

    @Override // j$.util.stream.IntStream
    public final boolean C(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.v(v, EnumCLASSNAMEk1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final boolean F(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.v(v, EnumCLASSNAMEk1.ANY))).booleanValue();
    }

    public void I(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, true));
    }

    @Override // j$.util.stream.IntStream
    public final Stream J(j$.util.function.m mVar) {
        mVar.getClass();
        return new L(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, mVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final j$.util.u K0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        return new CLASSNAMEq4(abstractCLASSNAMEy2, yVar, z);
    }

    @Override // j$.util.stream.IntStream
    public final int N(int i, j$.util.function.j jVar) {
        jVar.getClass();
        return ((Integer) x0(new L2(EnumCLASSNAMEe4.INT_VALUE, jVar, i))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream P(j$.util.function.m mVar) {
        return new M(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, mVar);
    }

    public void U(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, false));
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEk a0(j$.util.function.j jVar) {
        jVar.getClass();
        return (CLASSNAMEk) x0(new D2(EnumCLASSNAMEe4.INT_VALUE, jVar));
    }

    @Override // j$.util.stream.IntStream
    public final U asDoubleStream() {
        return new O(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n);
    }

    @Override // j$.util.stream.IntStream
    public final InterfaceCLASSNAMEe1 asLongStream() {
        return new G0(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n);
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEj average() {
        long[] jArr = (long[]) k0(CLASSNAMEv0.a, CLASSNAMEu0.a, CLASSNAMEx0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return CLASSNAMEj.d(d / d2);
        }
        return CLASSNAMEj.a();
    }

    @Override // j$.util.stream.IntStream
    public final Stream boxed() {
        return J(C0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream c0(j$.util.function.l lVar) {
        lVar.getClass();
        return new M(this, this, EnumCLASSNAMEe4.INT_VALUE, 0, lVar);
    }

    @Override // j$.util.stream.IntStream
    public final long count() {
        return ((AbstractCLASSNAMEd1) f(E0.a)).sum();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return ((AbstractCLASSNAMEe3) J(C0.a)).distinct().m(CLASSNAMEw0.a);
    }

    @Override // j$.util.stream.IntStream
    public final InterfaceCLASSNAMEe1 f(j$.util.function.n nVar) {
        nVar.getClass();
        return new N(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, nVar);
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEk findAny() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(false, EnumCLASSNAMEe4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEk findFirst() {
        return (CLASSNAMEk) x0(new CLASSNAMEd0(true, EnumCLASSNAMEe4.INT_VALUE, CLASSNAMEk.a(), X.a, CLASSNAMEa0.a));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream h(j$.wrappers.V v) {
        v.getClass();
        return new M(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.t, v);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public final p.a moNUMiterator() {
        return j$.util.L.g(moNUMspliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public Iterator moNUMiterator() {
        return j$.util.L.g(moNUMspliterator());
    }

    @Override // j$.util.stream.IntStream
    public final Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        yVar.getClass();
        vVar.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.INT_VALUE, c, vVar, yVar));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream limit(long j) {
        if (j >= 0) {
            return B3.g(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEk max() {
        return a0(CLASSNAMEz0.a);
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEk min() {
        return a0(A0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream q(CLASSNAMEb0 CLASSNAMEb0) {
        CLASSNAMEb0.getClass();
        return new M(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, CLASSNAMEb0);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.g(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream sorted() {
        return new K3(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public final u.a moNUMspliterator() {
        return M0(super.moNUMspliterator());
    }

    @Override // j$.util.stream.IntStream
    public final int sum() {
        return ((Integer) x0(new L2(EnumCLASSNAMEe4.INT_VALUE, CLASSNAMEy0.a, 0))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final CLASSNAMEh summaryStatistics() {
        return (CLASSNAMEh) k0(CLASSNAMEj.a, CLASSNAMEt0.a, CLASSNAMEs0.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEs1 t0(long j, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.p(j);
    }

    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return (int[]) AbstractCLASSNAMEx2.n((InterfaceCLASSNAMEw1) y0(D0.a)).e();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public InterfaceCLASSNAMEg unordered() {
        return !C0() ? this : new H0(this, this, EnumCLASSNAMEe4.INT_VALUE, EnumCLASSNAMEd4.r);
    }

    @Override // j$.util.stream.IntStream
    public final boolean v(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.v(v, EnumCLASSNAMEk1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final A1 z0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.g(abstractCLASSNAMEy2, uVar, z);
    }
}
