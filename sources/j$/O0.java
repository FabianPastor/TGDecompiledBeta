package j$;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEn;
import j$.util.CLASSNAMEo;
import j$.util.CLASSNAMEt;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.E;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEx2;
import j$.util.stream.L1;
import j$.util.stream.Stream;
import j$.util.stream.T2;
import java.util.stream.DoubleStream;

public final /* synthetic */ class O0 implements L1 {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ O0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ L1 m0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof P0 ? ((P0) doubleStream).a : new O0(doubleStream);
    }

    public /* synthetic */ CLASSNAMEt C(p pVar) {
        return CLASSNAMEk.k(this.a.reduce(C.a(pVar)));
    }

    public /* synthetic */ Object D(E e, B b, BiConsumer biConsumer) {
        return this.a.collect(D0.a(e), CLASSNAMEv0.a(b), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ double H(double d, p pVar) {
        return this.a.reduce(d, C.a(pVar));
    }

    public /* synthetic */ L1 I(N n) {
        return m0(this.a.map(O.a(n)));
    }

    public /* synthetic */ Stream J(r rVar) {
        return U0.m0(this.a.mapToObj(G.a(rVar)));
    }

    public /* synthetic */ L1 O(H h) {
        return m0(this.a.filter(I.a(h)));
    }

    public /* synthetic */ boolean V(H h) {
        return this.a.anyMatch(I.a(h));
    }

    public /* synthetic */ CLASSNAMEt average() {
        return CLASSNAMEk.k(this.a.average());
    }

    public /* synthetic */ boolean b(H h) {
        return this.a.noneMatch(I.a(h));
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.a.boxed());
    }

    public /* synthetic */ L1 c(q qVar) {
        return m0(this.a.peek(E.a(qVar)));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ L1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ CLASSNAMEt findAny() {
        return CLASSNAMEk.k(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEt findFirst() {
        return CLASSNAMEk.k(this.a.findFirst());
    }

    public /* synthetic */ boolean h0(H h) {
        return this.a.allMatch(I.a(h));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ void k(q qVar) {
        this.a.forEach(E.a(qVar));
    }

    public /* synthetic */ void k0(q qVar) {
        this.a.forEachOrdered(E.a(qVar));
    }

    public /* synthetic */ L1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEt max() {
        return CLASSNAMEk.k(this.a.max());
    }

    public /* synthetic */ CLASSNAMEt min() {
        return CLASSNAMEk.k(this.a.min());
    }

    public /* synthetic */ CLASSNAMEx2 o(J j) {
        return Q0.m0(this.a.mapToInt(j == null ? null : j.a));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ L1 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ L1 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    public /* synthetic */ CLASSNAMEn summaryStatistics() {
        return CLASSNAMEo.a(this.a.summaryStatistics());
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ L1 u(r rVar) {
        return m0(this.a.flatMap(G.a(rVar)));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }

    public /* synthetic */ T2 v(s sVar) {
        return S0.m0(this.a.mapToLong(M.a(sVar)));
    }
}
