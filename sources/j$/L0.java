package j$;

import j$.time.a;
import j$.util.function.BiConsumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.l;
import j$.util.o;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.DoubleStream;

public final /* synthetic */ class L0 implements CLASSNAMEs1 {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ L0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ CLASSNAMEs1 m0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof M0 ? ((M0) doubleStream).a : new L0(doubleStream);
    }

    public /* synthetic */ o C(p pVar) {
        return a.k(this.a.reduce(CLASSNAMEz.a(pVar)));
    }

    public /* synthetic */ Object D(J j, G g, BiConsumer biConsumer) {
        return this.a.collect(A0.a(j), s0.a(g), r.a(biConsumer));
    }

    public /* synthetic */ double G(double d, p pVar) {
        return this.a.reduce(d, CLASSNAMEz.a(pVar));
    }

    public /* synthetic */ CLASSNAMEs1 H(u uVar) {
        return m0(this.a.map(L.a(uVar)));
    }

    public /* synthetic */ Stream I(r rVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(D.a(rVar)));
    }

    public /* synthetic */ boolean J(s sVar) {
        return this.a.noneMatch(F.a(sVar));
    }

    public /* synthetic */ boolean O(s sVar) {
        return this.a.allMatch(F.a(sVar));
    }

    public /* synthetic */ boolean W(s sVar) {
        return this.a.anyMatch(F.a(sVar));
    }

    public /* synthetic */ o average() {
        return a.k(this.a.average());
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

    public /* synthetic */ CLASSNAMEs1 d(q qVar) {
        return m0(this.a.peek(B.a(qVar)));
    }

    public /* synthetic */ CLASSNAMEs1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ o findAny() {
        return a.k(this.a.findAny());
    }

    public /* synthetic */ o findFirst() {
        return a.k(this.a.findFirst());
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ void k0(q qVar) {
        this.a.forEachOrdered(B.a(qVar));
    }

    public /* synthetic */ void l(q qVar) {
        this.a.forEach(B.a(qVar));
    }

    public /* synthetic */ CLASSNAMEs1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ o max() {
        return a.k(this.a.max());
    }

    public /* synthetic */ o min() {
        return a.k(this.a.min());
    }

    public /* synthetic */ C1 o(G g) {
        return N0.m0(this.a.mapToInt(g == null ? null : g.a));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return H0.m0(this.a.onClose(runnable));
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

    public l summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ CLASSNAMEs1 t(s sVar) {
        return m0(this.a.filter(F.a(sVar)));
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEs1 u(r rVar) {
        return m0(this.a.flatMap(D.a(rVar)));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return H0.m0(this.a.unordered());
    }

    public /* synthetic */ H1 v(t tVar) {
        return P0.m0(this.a.mapToLong(J.a(tVar)));
    }
}
