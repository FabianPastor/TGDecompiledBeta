package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.function.BiConsumer;
import j$.util.function.d;
import j$.util.function.f;
import j$.util.function.g;
import j$.util.function.h;
import j$.util.function.u;
import j$.util.function.y;
import j$.util.stream.CLASSNAMEe1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import j$.util.stream.U;
import java.util.stream.DoubleStream;

public final /* synthetic */ class L0 implements U {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ L0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ U n0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof M0 ? ((M0) doubleStream).a : new L0(doubleStream);
    }

    public /* synthetic */ CLASSNAMEj G(d dVar) {
        return CLASSNAMEa.q(this.a.reduce(CLASSNAMEz.a(dVar)));
    }

    public /* synthetic */ Object H(y yVar, u uVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), s0.a(uVar), r.a(biConsumer));
    }

    public /* synthetic */ double K(double d, d dVar) {
        return this.a.reduce(d, CLASSNAMEz.a(dVar));
    }

    public /* synthetic */ Stream M(g gVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(D.a(gVar)));
    }

    public /* synthetic */ IntStream R(G g) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(g == null ? null : g.a));
    }

    public /* synthetic */ boolean Y(E e) {
        return this.a.allMatch(F.a(e));
    }

    public /* synthetic */ CLASSNAMEj average() {
        return CLASSNAMEa.q(this.a.average());
    }

    public /* synthetic */ U b(f fVar) {
        return n0(this.a.peek(B.a(fVar)));
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ U distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ CLASSNAMEj findAny() {
        return CLASSNAMEa.q(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEj findFirst() {
        return CLASSNAMEa.q(this.a.findFirst());
    }

    public /* synthetic */ boolean h0(E e) {
        return this.a.anyMatch(F.a(e));
    }

    public /* synthetic */ boolean i0(E e) {
        return this.a.noneMatch(F.a(e));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ void j(f fVar) {
        this.a.forEach(B.a(fVar));
    }

    public /* synthetic */ void l0(f fVar) {
        this.a.forEachOrdered(B.a(fVar));
    }

    public /* synthetic */ U limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEj max() {
        return CLASSNAMEa.q(this.a.max());
    }

    public /* synthetic */ CLASSNAMEj min() {
        return CLASSNAMEa.q(this.a.min());
    }

    public /* synthetic */ CLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ U r(E e) {
        return n0(this.a.filter(F.a(e)));
    }

    public /* synthetic */ U skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ U sorted() {
        return n0(this.a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    public CLASSNAMEg summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    public /* synthetic */ U w(g gVar) {
        return n0(this.a.flatMap(D.a(gVar)));
    }

    public /* synthetic */ CLASSNAMEe1 x(h hVar) {
        return N0.n0(this.a.mapToLong(J.a(hVar)));
    }

    public /* synthetic */ U y(K k) {
        return n0(this.a.map(L.a(k)));
    }
}
