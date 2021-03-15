package a;

import j$.util.k;
import j$.util.stream.CLASSNAMEs1;
import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final /* synthetic */ class P0 implements DoubleStream {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CLASSNAMEs1 var_a;

    private /* synthetic */ P0(CLASSNAMEs1 s1Var) {
        this.var_a = s1Var;
    }

    public static /* synthetic */ DoubleStream m0(CLASSNAMEs1 s1Var) {
        if (s1Var == null) {
            return null;
        }
        return s1Var instanceof O0 ? ((O0) s1Var).var_a : new P0(s1Var);
    }

    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.var_a.O(H.a(doublePredicate));
    }

    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.var_a.W(H.a(doublePredicate));
    }

    public /* synthetic */ OptionalDouble average() {
        return k.o(this.var_a.average());
    }

    public /* synthetic */ Stream boxed() {
        return V0.m0(this.var_a.boxed());
    }

    public /* synthetic */ void close() {
        this.var_a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.var_a.D(C0.a(supplier), CLASSNAMEu0.a(objDoubleConsumer), CLASSNAMEt.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.var_a.count();
    }

    public /* synthetic */ DoubleStream distinct() {
        return m0(this.var_a.distinct());
    }

    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return m0(this.var_a.t(H.a(doublePredicate)));
    }

    public /* synthetic */ OptionalDouble findAny() {
        return k.o(this.var_a.findAny());
    }

    public /* synthetic */ OptionalDouble findFirst() {
        return k.o(this.var_a.findFirst());
    }

    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return m0(this.var_a.u(F.a(doubleFunction)));
    }

    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.var_a.l(D.b(doubleConsumer));
    }

    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.var_a.k0(D.b(doubleConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ DoubleStream limit(long j) {
        return m0(this.var_a.limit(j));
    }

    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return m0(this.var_a.H(N.b(doubleUnaryOperator)));
    }

    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return R0.m0(this.var_a.o(J.b(doubleToIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return T0.m0(this.var_a.v(L.a(doubleToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return V0.m0(this.var_a.I(F.a(doubleFunction)));
    }

    public /* synthetic */ OptionalDouble max() {
        return k.o(this.var_a.max());
    }

    public /* synthetic */ OptionalDouble min() {
        return k.o(this.var_a.min());
    }

    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.var_a.J(H.a(doublePredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return L0.m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return m0(this.var_a.d(D.b(doubleConsumer)));
    }

    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.var_a.G(d, B.a(doubleBinaryOperator));
    }

    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return k.o(this.var_a.C(B.a(doubleBinaryOperator)));
    }

    public /* synthetic */ DoubleStream skip(long j) {
        return m0(this.var_a.skip(j));
    }

    public /* synthetic */ DoubleStream sorted() {
        return m0(this.var_a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.var_a.sum();
    }

    public DoubleSummaryStatistics summaryStatistics() {
        this.var_a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ double[] toArray() {
        return this.var_a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return L0.m0(this.var_a.unordered());
    }
}
