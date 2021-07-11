package j$;

import j$.time.a;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.m;
import j$.util.o;
import j$.util.p;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.IntStream;

public final /* synthetic */ class N0 implements C1 {
    final /* synthetic */ IntStream a;

    private /* synthetic */ N0(IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ C1 m0(IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof O0 ? ((O0) intStream).a : new N0(intStream);
    }

    public /* synthetic */ void E(w wVar) {
        this.a.forEachOrdered(S.a(wVar));
    }

    public /* synthetic */ Stream F(x xVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(U.a(xVar)));
    }

    public /* synthetic */ int K(int i, v vVar) {
        return this.a.reduce(i, P.a(vVar));
    }

    public /* synthetic */ boolean L(y yVar) {
        return this.a.allMatch(W.a(yVar));
    }

    public /* synthetic */ C1 M(x xVar) {
        return m0(this.a.flatMap(U.a(xVar)));
    }

    public /* synthetic */ void Q(w wVar) {
        this.a.forEach(S.a(wVar));
    }

    public /* synthetic */ boolean R(y yVar) {
        return this.a.noneMatch(W.a(yVar));
    }

    public /* synthetic */ C1 X(y yVar) {
        return m0(this.a.filter(W.a(yVar)));
    }

    public /* synthetic */ p Z(v vVar) {
        return a.l(this.a.reduce(P.a(vVar)));
    }

    public /* synthetic */ C1 a0(w wVar) {
        return m0(this.a.peek(S.a(wVar)));
    }

    public /* synthetic */ CLASSNAMEs1 asDoubleStream() {
        return L0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ H1 asLongStream() {
        return P0.m0(this.a.asLongStream());
    }

    public /* synthetic */ o average() {
        return a.k(this.a.average());
    }

    public /* synthetic */ boolean b(y yVar) {
        return this.a.anyMatch(W.a(yVar));
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

    public /* synthetic */ C1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ p findAny() {
        return a.l(this.a.findAny());
    }

    public /* synthetic */ p findFirst() {
        return a.l(this.a.findFirst());
    }

    public /* synthetic */ H1 h(z zVar) {
        return P0.m0(this.a.mapToLong(CLASSNAMEa0.a(zVar)));
    }

    public /* synthetic */ CLASSNAMEs1 i0(X x) {
        return L0.m0(this.a.mapToDouble(x == null ? null : x.a));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Object j0(J j, H h, BiConsumer biConsumer) {
        return this.a.collect(A0.a(j), u0.a(h), r.a(biConsumer));
    }

    public /* synthetic */ C1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ p max() {
        return a.l(this.a.max());
    }

    public /* synthetic */ p min() {
        return a.l(this.a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return H0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ C1 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ C1 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public m summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return H0.m0(this.a.unordered());
    }

    public /* synthetic */ C1 y(A a2) {
        return m0(this.a.map(CLASSNAMEc0.a(a2)));
    }
}
