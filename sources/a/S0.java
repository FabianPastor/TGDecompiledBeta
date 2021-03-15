package a;

import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.k;
import j$.util.o;
import j$.util.p;
import j$.util.r;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.LongStream;

public final /* synthetic */ class S0 implements H1 {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongStream var_a;

    private /* synthetic */ S0(LongStream longStream) {
        this.var_a = longStream;
    }

    public static /* synthetic */ H1 m0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof T0 ? ((T0) longStream).var_a : new S0(longStream);
    }

    public /* synthetic */ long A(long j, B b) {
        return this.var_a.reduce(j, CLASSNAMEh0.a(b));
    }

    public /* synthetic */ Stream N(D d) {
        return U0.m0(this.var_a.mapToObj(CLASSNAMEl0.a(d)));
    }

    public /* synthetic */ void Y(C c) {
        this.var_a.forEachOrdered(CLASSNAMEj0.a(c));
    }

    public /* synthetic */ CLASSNAMEs1 asDoubleStream() {
        return O0.m0(this.var_a.asDoubleStream());
    }

    public /* synthetic */ p average() {
        return k.k(this.var_a.average());
    }

    public /* synthetic */ boolean b0(E e) {
        return this.var_a.anyMatch(CLASSNAMEn0.a(e));
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.var_a.boxed());
    }

    public /* synthetic */ boolean c(E e) {
        return this.var_a.noneMatch(CLASSNAMEn0.a(e));
    }

    public /* synthetic */ void close() {
        this.var_a.close();
    }

    public /* synthetic */ long count() {
        return this.var_a.count();
    }

    public /* synthetic */ Object d0(J j, I i, BiConsumer biConsumer) {
        return this.var_a.collect(D0.a(j), z0.a(i), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ H1 distinct() {
        return m0(this.var_a.distinct());
    }

    public /* synthetic */ void f(C c) {
        this.var_a.forEach(CLASSNAMEj0.a(c));
    }

    public /* synthetic */ boolean f0(E e) {
        return this.var_a.allMatch(CLASSNAMEn0.a(e));
    }

    public /* synthetic */ r findAny() {
        return k.m(this.var_a.findAny());
    }

    public /* synthetic */ r findFirst() {
        return k.m(this.var_a.findFirst());
    }

    public /* synthetic */ H1 g0(E e) {
        return m0(this.var_a.filter(CLASSNAMEn0.a(e)));
    }

    public /* synthetic */ r i(B b) {
        return k.m(this.var_a.reduce(CLASSNAMEh0.a(b)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ CLASSNAMEs1 j(CLASSNAMEo0 o0Var) {
        return O0.m0(this.var_a.mapToDouble(o0Var == null ? null : o0Var.var_a));
    }

    public /* synthetic */ H1 limit(long j) {
        return m0(this.var_a.limit(j));
    }

    public /* synthetic */ r max() {
        return k.m(this.var_a.max());
    }

    public /* synthetic */ r min() {
        return k.m(this.var_a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ H1 q(C c) {
        return m0(this.var_a.peek(CLASSNAMEj0.a(c)));
    }

    public /* synthetic */ H1 r(D d) {
        return m0(this.var_a.flatMap(CLASSNAMEl0.a(d)));
    }

    public /* synthetic */ H1 skip(long j) {
        return m0(this.var_a.skip(j));
    }

    public /* synthetic */ H1 sorted() {
        return m0(this.var_a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.var_a.sum();
    }

    public o summaryStatistics() {
        this.var_a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    public /* synthetic */ long[] toArray() {
        return this.var_a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.var_a.unordered());
    }

    public /* synthetic */ C1 w(CLASSNAMEq0 q0Var) {
        return Q0.m0(this.var_a.mapToInt(q0Var == null ? null : q0Var.var_a));
    }

    public /* synthetic */ H1 x(F f) {
        return m0(this.var_a.map(CLASSNAMEt0.a(f)));
    }
}
