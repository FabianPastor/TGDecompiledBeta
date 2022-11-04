package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;
import java.util.Iterator;
/* renamed from: j$.util.stream.d1 */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEd1 extends AbstractCLASSNAMEc implements InterfaceCLASSNAMEe1 {
    public AbstractCLASSNAMEd1(AbstractCLASSNAMEc abstractCLASSNAMEc, int i) {
        super(abstractCLASSNAMEc, i);
    }

    public AbstractCLASSNAMEd1(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ j$.util.v L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static j$.util.v M0(j$.util.u uVar) {
        if (uVar instanceof j$.util.v) {
            return (j$.util.v) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
        }
        Q4.a(AbstractCLASSNAMEc.class, "using LongStream.adapt(Spliterator<Long> s)");
        throw null;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final void A0(j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        j$.util.function.q w0;
        j$.util.v M0 = M0(uVar);
        if (interfaceCLASSNAMEm3 instanceof j$.util.function.q) {
            w0 = (j$.util.function.q) interfaceCLASSNAMEm3;
        } else if (Q4.a) {
            Q4.a(AbstractCLASSNAMEc.class, "using LongStream.adapt(Sink<Long> s)");
            throw null;
        } else {
            w0 = new W0(interfaceCLASSNAMEm3);
        }
        while (!interfaceCLASSNAMEm3.o() && M0.i(w0)) {
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final EnumCLASSNAMEe4 B0() {
        return EnumCLASSNAMEe4.LONG_VALUE;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final long D(long j, j$.util.function.o oVar) {
        oVar.getClass();
        return ((Long) x0(new P2(EnumCLASSNAMEe4.LONG_VALUE, oVar, j))).longValue();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final j$.util.u K0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        return new s4(abstractCLASSNAMEy2, yVar, z);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final boolean L(CLASSNAMEj0 CLASSNAMEj0) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.w(CLASSNAMEj0, EnumCLASSNAMEk1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final U O(CLASSNAMEl0 CLASSNAMEl0) {
        CLASSNAMEl0.getClass();
        return new K(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, CLASSNAMEl0);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final Stream Q(j$.util.function.r rVar) {
        rVar.getClass();
        return new L(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, rVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final boolean S(CLASSNAMEj0 CLASSNAMEj0) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.w(CLASSNAMEj0, EnumCLASSNAMEk1.NONE))).booleanValue();
    }

    public void Z(j$.util.function.q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final U asDoubleStream() {
        return new O(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEj average() {
        long[] jArr = (long[]) f0(P0.a, O0.a, R0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return CLASSNAMEj.d(d / d2);
        }
        return CLASSNAMEj.a();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final Stream boxed() {
        return Q(X0.a);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final long count() {
        return ((AbstractCLASSNAMEd1) z(Y0.a)).sum();
    }

    public void d(j$.util.function.q qVar) {
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, false));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 distinct() {
        return ((AbstractCLASSNAMEe3) Q(X0.a)).distinct().g0(Q0.a);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final IntStream e0(CLASSNAMEn0 CLASSNAMEn0) {
        CLASSNAMEn0.getClass();
        return new M(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, CLASSNAMEn0);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 2);
        yVar.getClass();
        wVar.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.LONG_VALUE, c, wVar, yVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEl findAny() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(false, EnumCLASSNAMEe4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEl findFirst() {
        return (CLASSNAMEl) x0(new CLASSNAMEd0(true, EnumCLASSNAMEe4.LONG_VALUE, CLASSNAMEl.a(), Y.a, CLASSNAMEb0.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEl g(j$.util.function.o oVar) {
        oVar.getClass();
        return (CLASSNAMEl) x0(new D2(EnumCLASSNAMEe4.LONG_VALUE, oVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public final j$.util.r mo307iterator() {
        return j$.util.L.h(mo310spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public Iterator mo307iterator() {
        return j$.util.L.h(mo310spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final boolean k(CLASSNAMEj0 CLASSNAMEj0) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.w(CLASSNAMEj0, EnumCLASSNAMEk1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 limit(long j) {
        if (j >= 0) {
            return B3.h(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEl max() {
        return g(U0.a);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEl min() {
        return g(V0.a);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 p(j$.util.function.q qVar) {
        qVar.getClass();
        return new N(this, this, EnumCLASSNAMEe4.LONG_VALUE, 0, qVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 s(j$.util.function.r rVar) {
        return new N(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, rVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.h(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 sorted() {
        return new L3(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public final j$.util.v mo310spliterator() {
        return M0(super.mo310spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final long sum() {
        return ((Long) x0(new P2(EnumCLASSNAMEe4.LONG_VALUE, T0.a, 0L))).longValue();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final CLASSNAMEi summaryStatistics() {
        return (CLASSNAMEi) f0(CLASSNAMEk.a, N0.a, M0.a);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEs1 t0(long j, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.q(j);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final long[] toArray() {
        return (long[]) AbstractCLASSNAMEx2.o((InterfaceCLASSNAMEy1) y0(S0.a)).e();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 u(CLASSNAMEj0 CLASSNAMEj0) {
        CLASSNAMEj0.getClass();
        return new N(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.t, CLASSNAMEj0);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public InterfaceCLASSNAMEg unordered() {
        return !C0() ? this : new G0(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.r);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public final InterfaceCLASSNAMEe1 z(j$.util.function.t tVar) {
        tVar.getClass();
        return new N(this, this, EnumCLASSNAMEe4.LONG_VALUE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, tVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final A1 z0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.h(abstractCLASSNAMEy2, uVar, z);
    }
}
