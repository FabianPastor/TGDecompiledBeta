package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.stream.IntStream;
import j$.util.stream.InterfaceCLASSNAMEe1;
import j$.util.stream.InterfaceCLASSNAMEg;
import j$.util.stream.Stream;
import j$.util.u;
import java.util.Iterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$IntStream$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$IntStream$VWRP implements IntStream {
    final /* synthetic */ java.util.stream.IntStream a;

    private /* synthetic */ C$r8$wrapper$java$util$stream$IntStream$VWRP(java.util.stream.IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ IntStream convert(java.util.stream.IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof C$r8$wrapper$java$util$stream$IntStream$WRP ? ((C$r8$wrapper$java$util$stream$IntStream$WRP) intStream).a : new C$r8$wrapper$java$util$stream$IntStream$VWRP(intStream);
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ j$.util.stream.U A(X x) {
        return L0.n0(this.a.mapToDouble(x == null ? null : x.a));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ boolean C(V v) {
        return this.a.allMatch(W.a(v));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ boolean F(V v) {
        return this.a.anyMatch(W.a(v));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ void I(j$.util.function.l lVar) {
        this.a.forEachOrdered(S.a(lVar));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ Stream J(j$.util.function.m mVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(U.a(mVar)));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ int N(int i, j$.util.function.j jVar) {
        return this.a.reduce(i, P.a(jVar));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream P(j$.util.function.m mVar) {
        return convert(this.a.flatMap(U.a(mVar)));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ void U(j$.util.function.l lVar) {
        this.a.forEach(S.a(lVar));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEk a0(j$.util.function.j jVar) {
        return AbstractCLASSNAMEa.r(this.a.reduce(P.a(jVar)));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ j$.util.stream.U asDoubleStream() {
        return L0.n0(this.a.asDoubleStream());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ InterfaceCLASSNAMEe1 asLongStream() {
        return N0.n0(this.a.asLongStream());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEj average() {
        return AbstractCLASSNAMEa.q(this.a.average());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream c0(j$.util.function.l lVar) {
        return convert(this.a.peek(S.a(lVar)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream distinct() {
        return convert(this.a.distinct());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ InterfaceCLASSNAMEe1 f(j$.util.function.n nVar) {
        return N0.n0(this.a.mapToLong(CLASSNAMEa0.a(nVar)));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEk findAny() {
        return AbstractCLASSNAMEa.r(this.a.findAny());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEk findFirst() {
        return AbstractCLASSNAMEa.r(this.a.findFirst());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream h(V v) {
        return convert(this.a.filter(W.a(v)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ p.a moNUMiterator() {
        return CLASSNAMEc.a(this.a.iterator());
    }

    @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ Iterator moNUMiterator() {
        return this.a.iterator();
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), u0.a(vVar), r.a(biConsumer));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream limit(long j) {
        return convert(this.a.limit(j));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEk max() {
        return AbstractCLASSNAMEa.r(this.a.max());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ CLASSNAMEk min() {
        return AbstractCLASSNAMEa.r(this.a.min());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ IntStream moNUMparallel() {
        return convert(this.a.parallel());
    }

    @Override // j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ InterfaceCLASSNAMEg moNUMparallel() {
        return H0.n0(this.a.parallel());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream q(CLASSNAMEb0 CLASSNAMEb0) {
        return convert(this.a.map(AbstractCLASSNAMEc0.a(CLASSNAMEb0)));
    }

    @Override // j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ IntStream moNUMsequential() {
        return convert(this.a.sequential());
    }

    @Override // j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ InterfaceCLASSNAMEg moNUMsequential() {
        return H0.n0(this.a.sequential());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream skip(long j) {
        return convert(this.a.skip(j));
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ IntStream sorted() {
        return convert(this.a.sorted());
    }

    @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ u.a moNUMspliterator() {
        return CLASSNAMEk.a(this.a.spliterator());
    }

    @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.u moNUMspliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    @Override // j$.util.stream.IntStream
    public CLASSNAMEh summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    @Override // j$.util.stream.IntStream
    public /* synthetic */ boolean v(V v) {
        return this.a.noneMatch(W.a(v));
    }
}
