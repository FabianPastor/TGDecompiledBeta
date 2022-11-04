package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.InterfaceCLASSNAMEn;
import j$.util.function.BiConsumer;
import j$.util.stream.IntStream;
import j$.util.stream.InterfaceCLASSNAMEe1;
import j$.util.stream.InterfaceCLASSNAMEg;
import j$.util.stream.Stream;
import java.util.Iterator;
import java.util.stream.DoubleStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class L0 implements j$.util.stream.U {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ L0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ j$.util.stream.U n0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof M0 ? ((M0) doubleStream).a : new L0(doubleStream);
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj G(j$.util.function.d dVar) {
        return AbstractCLASSNAMEa.q(this.a.reduce(CLASSNAMEz.a(dVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), s0.a(uVar), r.a(biConsumer));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double K(double d, j$.util.function.d dVar) {
        return this.a.reduce(d, CLASSNAMEz.a(dVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Stream M(j$.util.function.g gVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(D.a(gVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ IntStream R(G g) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(g == null ? null : g.a));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean Y(E e) {
        return this.a.allMatch(F.a(e));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj average() {
        return AbstractCLASSNAMEa.q(this.a.average());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U b(j$.util.function.f fVar) {
        return n0(this.a.peek(B.a(fVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U distinct() {
        return n0(this.a.distinct());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj findAny() {
        return AbstractCLASSNAMEa.q(this.a.findAny());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj findFirst() {
        return AbstractCLASSNAMEa.q(this.a.findFirst());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean h0(E e) {
        return this.a.anyMatch(F.a(e));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean i0(E e) {
        return this.a.noneMatch(F.a(e));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ InterfaceCLASSNAMEn mo307iterator() {
        return CLASSNAMEa.a(this.a.iterator());
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ Iterator mo307iterator() {
        return this.a.iterator();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ void j(j$.util.function.f fVar) {
        this.a.forEach(B.a(fVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ void l0(j$.util.function.f fVar) {
        this.a.forEachOrdered(B.a(fVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj max() {
        return AbstractCLASSNAMEa.q(this.a.max());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ CLASSNAMEj min() {
        return AbstractCLASSNAMEa.q(this.a.min());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ j$.util.stream.U mo308parallel() {
        return n0(this.a.parallel());
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ InterfaceCLASSNAMEg mo308parallel() {
        return H0.n0(this.a.parallel());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U r(E e) {
        return n0(this.a.filter(F.a(e)));
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ j$.util.stream.U mo309sequential() {
        return n0(this.a.sequential());
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ InterfaceCLASSNAMEg mo309sequential() {
        return H0.n0(this.a.sequential());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U sorted() {
        return n0(this.a.sorted());
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.t mo310spliterator() {
        return CLASSNAMEi.a(this.a.spliterator());
    }

    @Override // j$.util.stream.U, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.u mo310spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    @Override // j$.util.stream.U
    public CLASSNAMEg summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U w(j$.util.function.g gVar) {
        return n0(this.a.flatMap(D.a(gVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ InterfaceCLASSNAMEe1 x(j$.util.function.h hVar) {
        return N0.n0(this.a.mapToLong(J.a(hVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U y(K k) {
        return n0(this.a.map(L.a(k)));
    }
}
