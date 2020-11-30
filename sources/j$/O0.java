package j$;

import j$.util.function.BiConsumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.k;
import j$.util.m;
import j$.util.p;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.DoubleStream;

public final /* synthetic */ class O0 implements CLASSNAMEs1 {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ O0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ CLASSNAMEs1 m0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof P0 ? ((P0) doubleStream).a : new O0(doubleStream);
    }

    public /* synthetic */ p C(j$.util.function.p pVar) {
        return k.k(this.a.reduce(C.a(pVar)));
    }

    public /* synthetic */ Object D(J j, G g, BiConsumer biConsumer) {
        return this.a.collect(D0.a(j), CLASSNAMEv0.a(g), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ double G(double d, j$.util.function.p pVar) {
        return this.a.reduce(d, C.a(pVar));
    }

    public /* synthetic */ CLASSNAMEs1 H(u uVar) {
        return m0(this.a.map(O.a(uVar)));
    }

    public /* synthetic */ Stream I(r rVar) {
        return U0.m0(this.a.mapToObj(G.a(rVar)));
    }

    public /* synthetic */ boolean J(s sVar) {
        return this.a.noneMatch(I.a(sVar));
    }

    public /* synthetic */ boolean O(s sVar) {
        return this.a.allMatch(I.a(sVar));
    }

    public /* synthetic */ boolean W(s sVar) {
        return this.a.anyMatch(I.a(sVar));
    }

    public /* synthetic */ p average() {
        return k.k(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ CLASSNAMEs1 d(q qVar) {
        return m0(this.a.peek(E.a(qVar)));
    }

    public /* synthetic */ CLASSNAMEs1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ p findAny() {
        return k.k(this.a.findAny());
    }

    public /* synthetic */ p findFirst() {
        return k.k(this.a.findFirst());
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ void k0(q qVar) {
        this.a.forEachOrdered(E.a(qVar));
    }

    public /* synthetic */ void l(q qVar) {
        this.a.forEach(E.a(qVar));
    }

    public /* synthetic */ CLASSNAMEs1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ p max() {
        return k.k(this.a.max());
    }

    public /* synthetic */ p min() {
        return k.k(this.a.min());
    }

    public /* synthetic */ C1 o(J j) {
        return Q0.m0(this.a.mapToInt(j == null ? null : j.a));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEs1 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ CLASSNAMEs1 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    public m summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ CLASSNAMEs1 t(s sVar) {
        return m0(this.a.filter(I.a(sVar)));
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEs1 u(r rVar) {
        return m0(this.a.flatMap(G.a(rVar)));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }

    public /* synthetic */ H1 v(t tVar) {
        return S0.m0(this.a.mapToLong(M.a(tVar)));
    }
}
