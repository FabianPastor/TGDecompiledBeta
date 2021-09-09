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
import j$.util.function.z;
import j$.util.stream.CLASSNAMEf1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.M0;
import j$.util.stream.Stream;
import j$.util.stream.U;
import java.util.stream.IntStream;

public final /* synthetic */ class N0 implements M0 {
    final /* synthetic */ IntStream a;

    private /* synthetic */ N0(IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ M0 n0(IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof O0 ? ((O0) intStream).a : new N0(intStream);
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

    public /* synthetic */ M0 P(m mVar) {
        return n0(this.a.flatMap(U.a(mVar)));
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

    public /* synthetic */ CLASSNAMEf1 asLongStream() {
        return P0.n0(this.a.asLongStream());
    }

    public /* synthetic */ CLASSNAMEj average() {
        return CLASSNAMEa.q(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    public /* synthetic */ M0 c0(l lVar) {
        return n0(this.a.peek(S.a(lVar)));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ M0 distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ CLASSNAMEf1 f(n nVar) {
        return P0.n0(this.a.mapToLong(CLASSNAMEa0.a(nVar)));
    }

    public /* synthetic */ CLASSNAMEk findAny() {
        return CLASSNAMEa.r(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEk findFirst() {
        return CLASSNAMEa.r(this.a.findFirst());
    }

    public /* synthetic */ M0 h(V v) {
        return n0(this.a.filter(W.a(v)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Object k0(z zVar, v vVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(zVar), u0.a(vVar), r.a(biConsumer));
    }

    public /* synthetic */ M0 limit(long j) {
        return n0(this.a.limit(j));
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

    public /* synthetic */ M0 q(CLASSNAMEb0 b0Var) {
        return n0(this.a.map(CLASSNAMEc0.a(b0Var)));
    }

    public /* synthetic */ M0 skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ M0 sorted() {
        return n0(this.a.sorted());
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
