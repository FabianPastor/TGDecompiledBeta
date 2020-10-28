package j$;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEs;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEv;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.r;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEx2;
import j$.util.stream.L1;
import j$.util.stream.Stream;
import j$.util.stream.T2;
import java.util.stream.LongStream;

public final /* synthetic */ class S0 implements T2 {
    final /* synthetic */ LongStream a;

    private /* synthetic */ S0(LongStream longStream) {
        this.a = longStream;
    }

    public static /* synthetic */ T2 m0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof T0 ? ((T0) longStream).a : new S0(longStream);
    }

    public /* synthetic */ long A(long j, x xVar) {
        return this.a.reduce(j, CLASSNAMEh0.a(xVar));
    }

    public /* synthetic */ T2 G(CLASSNAMEm0 m0Var) {
        return m0(this.a.filter(CLASSNAMEn0.a(m0Var)));
    }

    public /* synthetic */ Stream N(z zVar) {
        return U0.m0(this.a.mapToObj(CLASSNAMEl0.a(zVar)));
    }

    public /* synthetic */ void X(y yVar) {
        this.a.forEachOrdered(CLASSNAMEj0.a(yVar));
    }

    public /* synthetic */ L1 asDoubleStream() {
        return O0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ CLASSNAMEt average() {
        return CLASSNAMEk.k(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.a.boxed());
    }

    public /* synthetic */ Object c0(E e, D d, BiConsumer biConsumer) {
        return this.a.collect(D0.a(e), z0.a(d), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ T2 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ void e(y yVar) {
        this.a.forEach(CLASSNAMEj0.a(yVar));
    }

    public /* synthetic */ CLASSNAMEv findAny() {
        return CLASSNAMEk.m(this.a.findAny());
    }

    public /* synthetic */ CLASSNAMEv findFirst() {
        return CLASSNAMEk.m(this.a.findFirst());
    }

    public /* synthetic */ CLASSNAMEv h(x xVar) {
        return CLASSNAMEk.m(this.a.reduce(CLASSNAMEh0.a(xVar)));
    }

    public /* synthetic */ L1 i(CLASSNAMEo0 o0Var) {
        return O0.m0(this.a.mapToDouble(o0Var == null ? null : o0Var.a));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ boolean l(CLASSNAMEm0 m0Var) {
        return this.a.anyMatch(CLASSNAMEn0.a(m0Var));
    }

    public /* synthetic */ T2 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ CLASSNAMEv max() {
        return CLASSNAMEk.m(this.a.max());
    }

    public /* synthetic */ CLASSNAMEv min() {
        return CLASSNAMEk.m(this.a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ T2 q(y yVar) {
        return m0(this.a.peek(CLASSNAMEj0.a(yVar)));
    }

    public /* synthetic */ boolean r(CLASSNAMEm0 m0Var) {
        return this.a.noneMatch(CLASSNAMEn0.a(m0Var));
    }

    public /* synthetic */ T2 s(z zVar) {
        return m0(this.a.flatMap(CLASSNAMEl0.a(zVar)));
    }

    public /* synthetic */ T2 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ T2 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    public /* synthetic */ r summaryStatistics() {
        return CLASSNAMEs.a(this.a.summaryStatistics());
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }

    public /* synthetic */ CLASSNAMEx2 w(CLASSNAMEq0 q0Var) {
        return Q0.m0(this.a.mapToInt(q0Var == null ? null : q0Var.a));
    }

    public /* synthetic */ T2 x(A a2) {
        return m0(this.a.map(CLASSNAMEt0.a(a2)));
    }

    public /* synthetic */ boolean y(CLASSNAMEm0 m0Var) {
        return this.a.allMatch(CLASSNAMEn0.a(m0Var));
    }
}
