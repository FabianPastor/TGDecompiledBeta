package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.function.j;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.util.function.v;
import j$.util.function.y;
import j$.util.stream.CLASSNAMEe1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import j$.util.stream.U;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$IntStream$-V-WRP  reason: invalid class name */
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

    public /* synthetic */ U A(X x) {
        return L0.n0(this.a.mapToDouble(x == null ? null : x.a));
    }

    public /* synthetic */ boolean C(V v) {
        return this.a.allMatch(W.a(v));
    }

    public /* synthetic */ boolean F(V v) {
        return this.a.anyMatch(W.a(v));
    }

    public /* synthetic */ void I(l lVar) {
        this.a.forEachOrdered(S.a(lVar));
    }

    public /* synthetic */ Stream J(m mVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(U.a(mVar)));
    }

    public /* synthetic */ int N(int i, j jVar) {
        return this.a.reduce(i, P.a(jVar));
    }

    public /* synthetic */ IntStream P(m mVar) {
        return convert(this.a.flatMap(U.a(mVar)));
    }

    public /* synthetic */ void U(l lVar) {
        this.a.forEach(S.a(lVar));
    }

    public /* synthetic */ CLASSNAMEk a0(j jVar) {
        return CLASSNAMEa.r(this.a.reduce(P.a(jVar)));
    }

    public /* synthetic */ U asDoubleStream() {
        return L0.n0(this.a.asDoubleStream());
    }

    public /* synthetic */ CLASSNAMEe1 asLongStream() {
        return N0.n0(this.a.asLongStream());
    }

    public /* synthetic */ CLASSNAMEj average() {
        return CLASSNAMEa.q(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    public /* synthetic */ IntStream c0(l lVar) {
        return convert(this.a.peek(S.a(lVar)));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return convert(this.a.distinct());
    }

    public /* synthetic */ CLASSNAMEe1 f(n nVar) {
        return N0.n0(this.a.mapToLong(CLASSNAMEa0.a(nVar)));
    }

    public /* synthetic */ CLASSNAMEk findAny() {
        return CLASSNAMEa.r(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEk findFirst() {
        return CLASSNAMEa.r(this.a.findFirst());
    }

    public /* synthetic */ IntStream h(V v) {
        return convert(this.a.filter(W.a(v)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Object k0(y yVar, v vVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), u0.a(vVar), r.a(biConsumer));
    }

    public /* synthetic */ IntStream limit(long j) {
        return convert(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEk max() {
        return CLASSNAMEa.r(this.a.max());
    }

    public /* synthetic */ CLASSNAMEk min() {
        return CLASSNAMEa.r(this.a.min());
    }

    public /* synthetic */ CLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream q(CLASSNAMEb0 b0Var) {
        return convert(this.a.map(CLASSNAMEc0.a(b0Var)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return convert(this.a.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return convert(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public CLASSNAMEh summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    public /* synthetic */ boolean v(V v) {
        return this.a.noneMatch(W.a(v));
    }
}
