package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.InterfaceCLASSNAMEn;
import j$.util.function.BiConsumer;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class T extends AbstractCLASSNAMEc implements U {
    public T(AbstractCLASSNAMEc abstractCLASSNAMEc, int i) {
        super(abstractCLASSNAMEc, i);
    }

    public T(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ j$.util.t L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static j$.util.t M0(j$.util.u uVar) {
        if (uVar instanceof j$.util.t) {
            return (j$.util.t) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
        }
        Q4.a(AbstractCLASSNAMEc.class, "using DoubleStream.adapt(Spliterator<Double> s)");
        throw null;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final void A0(j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        j$.util.function.f f;
        j$.util.t M0 = M0(uVar);
        if (interfaceCLASSNAMEm3 instanceof j$.util.function.f) {
            f = (j$.util.function.f) interfaceCLASSNAMEm3;
        } else if (Q4.a) {
            Q4.a(AbstractCLASSNAMEc.class, "using DoubleStream.adapt(Sink<Double> s)");
            throw null;
        } else {
            f = new F(interfaceCLASSNAMEm3);
        }
        while (!interfaceCLASSNAMEm3.o() && M0.k(f)) {
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final EnumCLASSNAMEe4 B0() {
        return EnumCLASSNAMEe4.DOUBLE_VALUE;
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj G(j$.util.function.d dVar) {
        dVar.getClass();
        return (CLASSNAMEj) x0(new D2(EnumCLASSNAMEe4.DOUBLE_VALUE, dVar));
    }

    @Override // j$.util.stream.U
    public final Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 0);
        yVar.getClass();
        uVar.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.DOUBLE_VALUE, c, uVar, yVar));
    }

    @Override // j$.util.stream.U
    public final double K(double d, j$.util.function.d dVar) {
        dVar.getClass();
        return ((Double) x0(new B2(EnumCLASSNAMEe4.DOUBLE_VALUE, dVar, d))).doubleValue();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final j$.util.u K0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        return new CLASSNAMEo4(abstractCLASSNAMEy2, yVar, z);
    }

    @Override // j$.util.stream.U
    public final Stream M(j$.util.function.g gVar) {
        gVar.getClass();
        return new L(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, gVar);
    }

    @Override // j$.util.stream.U
    public final IntStream R(j$.wrappers.G g) {
        g.getClass();
        return new M(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, g);
    }

    @Override // j$.util.stream.U
    public final boolean Y(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.u(e, EnumCLASSNAMEk1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj average() {
        double[] dArr = (double[]) H(CLASSNAMEx.a, CLASSNAMEv.a, A.a);
        return dArr[2] > 0.0d ? CLASSNAMEj.d(AbstractCLASSNAMEl.a(dArr) / dArr[2]) : CLASSNAMEj.a();
    }

    @Override // j$.util.stream.U
    public final U b(j$.util.function.f fVar) {
        fVar.getClass();
        return new K(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, 0, fVar);
    }

    @Override // j$.util.stream.U
    public final Stream boxed() {
        return M(G.a);
    }

    @Override // j$.util.stream.U
    public final long count() {
        return ((AbstractCLASSNAMEd1) x(H.a)).sum();
    }

    @Override // j$.util.stream.U
    public final U distinct() {
        return ((AbstractCLASSNAMEe3) M(G.a)).distinct().j0(CLASSNAMEz.a);
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj findAny() {
        return (CLASSNAMEj) x0(new CLASSNAMEd0(false, EnumCLASSNAMEe4.DOUBLE_VALUE, CLASSNAMEj.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj findFirst() {
        return (CLASSNAMEj) x0(new CLASSNAMEd0(true, EnumCLASSNAMEe4.DOUBLE_VALUE, CLASSNAMEj.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final boolean h0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.u(e, EnumCLASSNAMEk1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final boolean i0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.u(e, EnumCLASSNAMEk1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public final InterfaceCLASSNAMEn moNUMiterator() {
        return j$.util.L.f(moNUMspliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public Iterator moNUMiterator() {
        return j$.util.L.f(moNUMspliterator());
    }

    public void j(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new CLASSNAMEk0(fVar, false));
    }

    public void l0(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new CLASSNAMEk0(fVar, true));
    }

    @Override // j$.util.stream.U
    public final U limit(long j) {
        if (j >= 0) {
            return B3.f(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj max() {
        return G(D.a);
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEj min() {
        return G(E.a);
    }

    @Override // j$.util.stream.U
    public final U r(j$.wrappers.E e) {
        e.getClass();
        return new K(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.t, e);
    }

    @Override // j$.util.stream.U
    public final U skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.f(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.U
    public final U sorted() {
        return new J3(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public final j$.util.t moNUMspliterator() {
        return M0(super.moNUMspliterator());
    }

    @Override // j$.util.stream.U
    public final double sum() {
        return AbstractCLASSNAMEl.a((double[]) H(CLASSNAMEy.a, CLASSNAMEw.a, B.a));
    }

    @Override // j$.util.stream.U
    public final CLASSNAMEg summaryStatistics() {
        return (CLASSNAMEg) H(CLASSNAMEi.a, CLASSNAMEu.a, CLASSNAMEt.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEs1 t0(long j, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.j(j);
    }

    @Override // j$.util.stream.U
    public final double[] toArray() {
        return (double[]) AbstractCLASSNAMEx2.m((InterfaceCLASSNAMEu1) y0(I.a)).e();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public InterfaceCLASSNAMEg unordered() {
        return !C0() ? this : new O(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.r);
    }

    @Override // j$.util.stream.U
    public final U w(j$.util.function.g gVar) {
        return new K(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, gVar);
    }

    @Override // j$.util.stream.U
    public final InterfaceCLASSNAMEe1 x(j$.util.function.h hVar) {
        hVar.getClass();
        return new N(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, hVar);
    }

    @Override // j$.util.stream.U
    public final U y(j$.wrappers.K k) {
        k.getClass();
        return new K(this, this, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, k);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final A1 z0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.f(abstractCLASSNAMEy2, uVar, z);
    }
}
