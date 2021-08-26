package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.util.function.n;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.s;
import j$.util.function.v;
import j$.util.function.y;
import j$.util.stream.CLASSNAMEf1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.M0;
import j$.util.stream.Stream;
import j$.util.stream.U;
import java.util.stream.LongStream;

public final /* synthetic */ class P0 implements CLASSNAMEf1 {
    final /* synthetic */ LongStream a;

    private /* synthetic */ P0(LongStream longStream) {
        this.a = longStream;
    }

    public static /* synthetic */ CLASSNAMEf1 n0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof Q0 ? ((Q0) longStream).a : new P0(longStream);
    }

    public /* synthetic */ long D(long j, n nVar) {
        return this.a.reduce(j, CLASSNAMEe0.a(nVar));
    }

    public /* synthetic */ boolean L(CLASSNAMEj0 j0Var) {
        return this.a.allMatch(CLASSNAMEk0.a(j0Var));
    }

    public /* synthetic */ U O(CLASSNAMEl0 l0Var) {
        return L0.n0(this.a.mapToDouble(l0Var == null ? null : l0Var.a));
    }

    public /* synthetic */ Stream Q(q qVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(CLASSNAMEi0.a(qVar)));
    }

    public /* synthetic */ boolean S(CLASSNAMEj0 j0Var) {
        return this.a.noneMatch(CLASSNAMEk0.a(j0Var));
    }

    public /* synthetic */ void Z(p pVar) {
        this.a.forEachOrdered(CLASSNAMEg0.a(pVar));
    }

    public /* synthetic */ U asDoubleStream() {
        return L0.n0(this.a.asDoubleStream());
    }

    public /* synthetic */ CLASSNAMEj average() {
        return CLASSNAMEa.q(this.a.average());
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

    public /* synthetic */ void d(p pVar) {
        this.a.forEach(CLASSNAMEg0.a(pVar));
    }

    public /* synthetic */ CLASSNAMEf1 distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ M0 e0(CLASSNAMEn0 n0Var) {
        return N0.n0(this.a.mapToInt(n0Var == null ? null : n0Var.a));
    }

    public /* synthetic */ Object f0(y yVar, v vVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), w0.a(vVar), r.a(biConsumer));
    }

    public /* synthetic */ CLASSNAMEl findAny() {
        return CLASSNAMEa.s(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEl findFirst() {
        return CLASSNAMEa.s(this.a.findFirst());
    }

    public /* synthetic */ CLASSNAMEl g(n nVar) {
        return CLASSNAMEa.s(this.a.reduce(CLASSNAMEe0.a(nVar)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ boolean k(CLASSNAMEj0 j0Var) {
        return this.a.anyMatch(CLASSNAMEk0.a(j0Var));
    }

    public /* synthetic */ CLASSNAMEf1 limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEl max() {
        return CLASSNAMEa.s(this.a.max());
    }

    public /* synthetic */ CLASSNAMEl min() {
        return CLASSNAMEa.s(this.a.min());
    }

    public /* synthetic */ CLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEf1 p(p pVar) {
        return n0(this.a.peek(CLASSNAMEg0.a(pVar)));
    }

    public /* synthetic */ CLASSNAMEf1 s(q qVar) {
        return n0(this.a.flatMap(CLASSNAMEi0.a(qVar)));
    }

    public /* synthetic */ CLASSNAMEf1 skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ CLASSNAMEf1 sorted() {
        return n0(this.a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    public CLASSNAMEi summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEf1 u(CLASSNAMEj0 j0Var) {
        return n0(this.a.filter(CLASSNAMEk0.a(j0Var)));
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }

    public /* synthetic */ CLASSNAMEf1 z(s sVar) {
        return n0(this.a.map(CLASSNAMEq0.a(sVar)));
    }
}
