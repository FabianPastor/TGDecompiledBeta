package a;

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

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleStream var_a;

    private /* synthetic */ O0(DoubleStream doubleStream) {
        this.var_a = doubleStream;
    }

    public static /* synthetic */ CLASSNAMEs1 m0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof P0 ? ((P0) doubleStream).var_a : new O0(doubleStream);
    }

    public /* synthetic */ p C(j$.util.function.p pVar) {
        return k.k(this.var_a.reduce(C.a(pVar)));
    }

    public /* synthetic */ Object D(J j, G g, BiConsumer biConsumer) {
        return this.var_a.collect(D0.a(j), CLASSNAMEv0.a(g), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ double G(double d, j$.util.function.p pVar) {
        return this.var_a.reduce(d, C.a(pVar));
    }

    public /* synthetic */ CLASSNAMEs1 H(u uVar) {
        return m0(this.var_a.map(O.a(uVar)));
    }

    public /* synthetic */ Stream I(r rVar) {
        return U0.m0(this.var_a.mapToObj(G.a(rVar)));
    }

    public /* synthetic */ boolean J(s sVar) {
        return this.var_a.noneMatch(I.a(sVar));
    }

    public /* synthetic */ boolean O(s sVar) {
        return this.var_a.allMatch(I.a(sVar));
    }

    public /* synthetic */ boolean W(s sVar) {
        return this.var_a.anyMatch(I.a(sVar));
    }

    public /* synthetic */ p average() {
        return k.k(this.var_a.average());
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.var_a.boxed());
    }

    public /* synthetic */ void close() {
        this.var_a.close();
    }

    public /* synthetic */ long count() {
        return this.var_a.count();
    }

    public /* synthetic */ CLASSNAMEs1 d(q qVar) {
        return m0(this.var_a.peek(E.a(qVar)));
    }

    public /* synthetic */ CLASSNAMEs1 distinct() {
        return m0(this.var_a.distinct());
    }

    public /* synthetic */ p findAny() {
        return k.k(this.var_a.findAny());
    }

    public /* synthetic */ p findFirst() {
        return k.k(this.var_a.findFirst());
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ void k0(q qVar) {
        this.var_a.forEachOrdered(E.a(qVar));
    }

    public /* synthetic */ void l(q qVar) {
        this.var_a.forEach(E.a(qVar));
    }

    public /* synthetic */ CLASSNAMEs1 limit(long j) {
        return m0(this.var_a.limit(j));
    }

    public /* synthetic */ p max() {
        return k.k(this.var_a.max());
    }

    public /* synthetic */ p min() {
        return k.k(this.var_a.min());
    }

    public /* synthetic */ C1 o(J j) {
        return Q0.m0(this.var_a.mapToInt(j == null ? null : j.var_a));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEs1 skip(long j) {
        return m0(this.var_a.skip(j));
    }

    public /* synthetic */ CLASSNAMEs1 sorted() {
        return m0(this.var_a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.var_a.sum();
    }

    public m summaryStatistics() {
        this.var_a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ CLASSNAMEs1 t(s sVar) {
        return m0(this.var_a.filter(I.a(sVar)));
    }

    public /* synthetic */ double[] toArray() {
        return this.var_a.toArray();
    }

    public /* synthetic */ CLASSNAMEs1 u(r rVar) {
        return m0(this.var_a.flatMap(G.a(rVar)));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.var_a.unordered());
    }

    public /* synthetic */ H1 v(t tVar) {
        return S0.m0(this.var_a.mapToLong(M.a(tVar)));
    }
}
