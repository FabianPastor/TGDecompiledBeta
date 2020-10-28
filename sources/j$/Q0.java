package j$;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEp;
import j$.util.CLASSNAMEq;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEu;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.E;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEx2;
import j$.util.stream.L1;
import j$.util.stream.Stream;
import j$.util.stream.T2;
import java.util.stream.IntStream;

public final /* synthetic */ class Q0 implements CLASSNAMEx2 {
    final /* synthetic */ IntStream a;

    private /* synthetic */ Q0(IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ CLASSNAMEx2 m0(IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof R0 ? ((R0) intStream).a : new Q0(intStream);
    }

    public /* synthetic */ void E(u uVar) {
        this.a.forEachOrdered(V.a(uVar));
    }

    public /* synthetic */ Stream F(v vVar) {
        return U0.m0(this.a.mapToObj(X.a(vVar)));
    }

    public /* synthetic */ int K(int i, t tVar) {
        return this.a.reduce(i, T.a(tVar));
    }

    public /* synthetic */ boolean L(Y y) {
        return this.a.allMatch(Z.a(y));
    }

    public /* synthetic */ CLASSNAMEx2 M(v vVar) {
        return m0(this.a.flatMap(X.a(vVar)));
    }

    public /* synthetic */ void Q(u uVar) {
        this.a.forEach(V.a(uVar));
    }

    public /* synthetic */ CLASSNAMEx2 W(CLASSNAMEe0 e0Var) {
        return m0(this.a.map(CLASSNAMEf0.a(e0Var)));
    }

    public /* synthetic */ CLASSNAMEu Y(t tVar) {
        return CLASSNAMEk.l(this.a.reduce(T.a(tVar)));
    }

    public /* synthetic */ CLASSNAMEx2 Z(Y y) {
        return m0(this.a.filter(Z.a(y)));
    }

    public /* synthetic */ CLASSNAMEx2 a0(u uVar) {
        return m0(this.a.peek(V.a(uVar)));
    }

    public /* synthetic */ L1 asDoubleStream() {
        return O0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ T2 asLongStream() {
        return S0.m0(this.a.asLongStream());
    }

    public /* synthetic */ CLASSNAMEt average() {
        return CLASSNAMEk.k(this.a.average());
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

    public /* synthetic */ CLASSNAMEx2 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ boolean e0(Y y) {
        return this.a.anyMatch(Z.a(y));
    }

    public /* synthetic */ CLASSNAMEu findAny() {
        return CLASSNAMEk.l(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEu findFirst() {
        return CLASSNAMEk.l(this.a.findFirst());
    }

    public /* synthetic */ T2 g(w wVar) {
        return S0.m0(this.a.mapToLong(CLASSNAMEd0.a(wVar)));
    }

    public /* synthetic */ L1 g0(CLASSNAMEa0 a0Var) {
        return O0.m0(this.a.mapToDouble(a0Var == null ? null : a0Var.a));
    }

    public /* synthetic */ boolean i0(Y y) {
        return this.a.noneMatch(Z.a(y));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Object j0(E e, C c, BiConsumer biConsumer) {
        return this.a.collect(D0.a(e), x0.a(c), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ CLASSNAMEx2 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEu max() {
        return CLASSNAMEk.l(this.a.max());
    }

    public /* synthetic */ CLASSNAMEu min() {
        return CLASSNAMEk.l(this.a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEx2 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ CLASSNAMEx2 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public /* synthetic */ CLASSNAMEp summaryStatistics() {
        return CLASSNAMEq.a(this.a.summaryStatistics());
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }
}
