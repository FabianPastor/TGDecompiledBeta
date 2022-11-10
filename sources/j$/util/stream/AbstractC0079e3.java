package j$.util.stream;

import j$.util.Optional;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.e3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEe3 extends AbstractCLASSNAMEc implements Stream {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEe3(AbstractCLASSNAMEc abstractCLASSNAMEc, int i) {
        super(abstractCLASSNAMEc, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEe3(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final void A0(j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        while (!interfaceCLASSNAMEm3.o() && uVar.b(interfaceCLASSNAMEm3)) {
        }
    }

    @Override // j$.util.stream.Stream
    public final Object B(Object obj, BiFunction biFunction, j$.util.function.b bVar) {
        biFunction.getClass();
        bVar.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.REFERENCE, bVar, biFunction, obj));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final EnumCLASSNAMEe4 B0() {
        return EnumCLASSNAMEe4.REFERENCE;
    }

    @Override // j$.util.stream.Stream
    public final U E(Function function) {
        function.getClass();
        return new K(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, function);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final j$.util.u K0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        return new L4(abstractCLASSNAMEy2, yVar, z);
    }

    @Override // j$.util.stream.Stream
    public final Stream T(Predicate predicate) {
        predicate.getClass();
        return new L(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.t, predicate);
    }

    @Override // j$.util.stream.Stream
    public final Stream V(Consumer consumer) {
        consumer.getClass();
        return new L(this, this, EnumCLASSNAMEe4.REFERENCE, 0, consumer);
    }

    @Override // j$.util.stream.Stream
    public final boolean W(Predicate predicate) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.x(predicate, EnumCLASSNAMEk1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final InterfaceCLASSNAMEe1 X(Function function) {
        function.getClass();
        return new N(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, function);
    }

    @Override // j$.util.stream.Stream
    public final boolean a(Predicate predicate) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.x(predicate, EnumCLASSNAMEk1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final Object b0(j$.wrappers.J0 j0) {
        Object x0;
        if (!isParallel() || !j0.b().contains(EnumCLASSNAMEh.CONCURRENT) || (C0() && !j0.b().contains(EnumCLASSNAMEh.UNORDERED))) {
            j0.getClass();
            j$.util.function.y f = j0.f();
            x0 = x0(new I2(EnumCLASSNAMEe4.REFERENCE, j0.c(), j0.a(), f, j0));
        } else {
            x0 = j0.f().get();
            forEach(new CLASSNAMEo(j0.a(), x0));
        }
        return j0.b().contains(EnumCLASSNAMEh.IDENTITY_FINISH) ? x0 : j0.e().apply(x0);
    }

    @Override // j$.util.stream.Stream
    public final IntStream c(Function function) {
        function.getClass();
        return new M(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, function);
    }

    @Override // j$.util.stream.Stream
    public final long count() {
        return ((AbstractCLASSNAMEd1) g0(X2.a)).sum();
    }

    @Override // j$.util.stream.Stream
    public final boolean d0(Predicate predicate) {
        return ((Boolean) x0(AbstractCLASSNAMEo1.x(predicate, EnumCLASSNAMEk1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final Stream distinct() {
        return new CLASSNAMEs(this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.m | EnumCLASSNAMEd4.t);
    }

    public void e(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, true));
    }

    @Override // j$.util.stream.Stream
    public final Optional findAny() {
        return (Optional) x0(new CLASSNAMEd0(false, EnumCLASSNAMEe4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    @Override // j$.util.stream.Stream
    public final Optional findFirst() {
        return (Optional) x0(new CLASSNAMEd0(true, EnumCLASSNAMEe4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, false));
    }

    @Override // j$.util.stream.Stream
    public final InterfaceCLASSNAMEe1 g0(j$.util.function.A a) {
        a.getClass();
        return new N(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, a);
    }

    @Override // j$.util.stream.Stream
    public final Object i(j$.util.function.y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        yVar.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.REFERENCE, biConsumer2, biConsumer, yVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public final Iterator moNUMiterator() {
        return j$.util.L.i(moNUMspliterator());
    }

    @Override // j$.util.stream.Stream
    public final U j0(j$.util.function.z zVar) {
        zVar.getClass();
        return new K(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, zVar);
    }

    @Override // j$.util.stream.Stream
    public final Object[] l(j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.l(y0(mVar), mVar).q(mVar);
    }

    @Override // j$.util.stream.Stream
    public final Stream limit(long j) {
        if (j >= 0) {
            return B3.i(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.Stream
    public final IntStream m(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new M(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, toIntFunction);
    }

    @Override // j$.util.stream.Stream
    public final Object m0(Object obj, j$.util.function.b bVar) {
        bVar.getClass();
        return x0(new CLASSNAMEz2(EnumCLASSNAMEe4.REFERENCE, bVar, bVar, obj));
    }

    @Override // j$.util.stream.Stream
    public final Optional max(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEa(comparator, 0));
    }

    @Override // j$.util.stream.Stream
    public final Optional min(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEa(comparator, 1));
    }

    @Override // j$.util.stream.Stream
    public final Stream n(Function function) {
        function.getClass();
        return new CLASSNAMEa3(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n, function, 0);
    }

    @Override // j$.util.stream.Stream
    public final Stream o(Function function) {
        function.getClass();
        return new CLASSNAMEa3(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.p | EnumCLASSNAMEd4.n | EnumCLASSNAMEd4.t, function, 1);
    }

    @Override // j$.util.stream.Stream
    public final Stream skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.i(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted() {
        return new M3(this);
    }

    @Override // j$.util.stream.Stream
    public final Optional t(j$.util.function.b bVar) {
        bVar.getClass();
        return (Optional) x0(new D2(EnumCLASSNAMEe4.REFERENCE, bVar));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEs1 t0(long j, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.d(j, mVar);
    }

    @Override // j$.util.stream.Stream
    public final Object[] toArray() {
        W2 w2 = W2.a;
        return AbstractCLASSNAMEx2.l(y0(w2), w2).q(w2);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public InterfaceCLASSNAMEg unordered() {
        return !C0() ? this : new Z2(this, this, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.r);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final A1 z0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractCLASSNAMEx2.e(abstractCLASSNAMEy2, uVar, z, mVar);
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted(Comparator comparator) {
        return new M3(this, comparator);
    }
}
