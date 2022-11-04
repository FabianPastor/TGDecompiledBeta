package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.util.stream.IntStream;
import j$.util.stream.InterfaceCLASSNAMEe1;
import j$.util.stream.InterfaceCLASSNAMEg;
import j$.util.stream.Stream;
import java.util.Iterator;
import java.util.stream.LongStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class N0 implements InterfaceCLASSNAMEe1 {
    final /* synthetic */ LongStream a;

    private /* synthetic */ N0(LongStream longStream) {
        this.a = longStream;
    }

    public static /* synthetic */ InterfaceCLASSNAMEe1 n0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof O0 ? ((O0) longStream).a : new N0(longStream);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ long D(long j, j$.util.function.o oVar) {
        return this.a.reduce(j, CLASSNAMEe0.a(oVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ boolean L(CLASSNAMEj0 CLASSNAMEj0) {
        return this.a.allMatch(AbstractCLASSNAMEk0.a(CLASSNAMEj0));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ j$.util.stream.U O(CLASSNAMEl0 CLASSNAMEl0) {
        return L0.n0(this.a.mapToDouble(CLASSNAMEl0 == null ? null : CLASSNAMEl0.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ Stream Q(j$.util.function.r rVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(CLASSNAMEi0.a(rVar)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ boolean S(CLASSNAMEj0 CLASSNAMEj0) {
        return this.a.noneMatch(AbstractCLASSNAMEk0.a(CLASSNAMEj0));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ void Z(j$.util.function.q qVar) {
        this.a.forEachOrdered(CLASSNAMEg0.a(qVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ j$.util.stream.U asDoubleStream() {
        return L0.n0(this.a.asDoubleStream());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEj average() {
        return AbstractCLASSNAMEa.q(this.a.average());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ void d(j$.util.function.q qVar) {
        this.a.forEach(CLASSNAMEg0.a(qVar));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 distinct() {
        return n0(this.a.distinct());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ IntStream e0(CLASSNAMEn0 CLASSNAMEn0) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(CLASSNAMEn0 == null ? null : CLASSNAMEn0.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), w0.a(wVar), r.a(biConsumer));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEl findAny() {
        return AbstractCLASSNAMEa.s(this.a.findAny());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEl findFirst() {
        return AbstractCLASSNAMEa.s(this.a.findFirst());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEl g(j$.util.function.o oVar) {
        return AbstractCLASSNAMEa.s(this.a.reduce(CLASSNAMEe0.a(oVar)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ j$.util.r mo307iterator() {
        return CLASSNAMEe.a(this.a.iterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ Iterator mo307iterator() {
        return this.a.iterator();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ boolean k(CLASSNAMEj0 CLASSNAMEj0) {
        return this.a.anyMatch(AbstractCLASSNAMEk0.a(CLASSNAMEj0));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEl max() {
        return AbstractCLASSNAMEa.s(this.a.max());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ CLASSNAMEl min() {
        return AbstractCLASSNAMEa.s(this.a.min());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 p(j$.util.function.q qVar) {
        return n0(this.a.peek(CLASSNAMEg0.a(qVar)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ InterfaceCLASSNAMEe1 mo308parallel() {
        return n0(this.a.parallel());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ InterfaceCLASSNAMEg mo308parallel() {
        return H0.n0(this.a.parallel());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 s(j$.util.function.r rVar) {
        return n0(this.a.flatMap(CLASSNAMEi0.a(rVar)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ InterfaceCLASSNAMEe1 mo309sequential() {
        return n0(this.a.sequential());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ InterfaceCLASSNAMEg mo309sequential() {
        return H0.n0(this.a.sequential());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 sorted() {
        return n0(this.a.sorted());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.u mo310spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1, j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.v mo310spliterator() {
        return CLASSNAMEm.a(this.a.spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public CLASSNAMEi summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 u(CLASSNAMEj0 CLASSNAMEj0) {
        return n0(this.a.filter(AbstractCLASSNAMEk0.a(CLASSNAMEj0)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEe1
    public /* synthetic */ InterfaceCLASSNAMEe1 z(j$.util.function.t tVar) {
        return n0(this.a.map(q0.a(tVar)));
    }
}
