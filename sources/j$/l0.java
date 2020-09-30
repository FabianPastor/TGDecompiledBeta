package j$;

import j$.util.A;
import j$.util.r;
import j$.util.stream.M1;
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

public final /* synthetic */ class l0 implements DoubleStream {
    final /* synthetic */ M1 a;

    private /* synthetic */ l0(M1 m1) {
        this.a = m1;
    }

    public static /* synthetic */ DoubleStream c(M1 m1) {
        if (m1 == null) {
            return null;
        }
        return new l0(m1);
    }

    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.a.Q(CLASSNAMEx.b(doublePredicate));
    }

    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.a.Z(CLASSNAMEx.b(doublePredicate));
    }

    public /* synthetic */ OptionalDouble average() {
        return A.b(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return o0.c(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.a.E(f0.a(supplier), a0.b(objDoubleConsumer), CLASSNAMEn.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ DoubleStream distinct() {
        return c(this.a.distinct());
    }

    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return c(this.a.v(CLASSNAMEx.b(doublePredicate)));
    }

    public /* synthetic */ OptionalDouble findAny() {
        return A.b(this.a.findAny());
    }

    public /* synthetic */ OptionalDouble findFirst() {
        return A.b(this.a.findFirst());
    }

    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return c(this.a.w(CLASSNAMEw.b(doubleFunction)));
    }

    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.a.n(CLASSNAMEu.a(doubleConsumer));
    }

    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.a.m0(CLASSNAMEu.a(doubleConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ DoubleStream limit(long j) {
        return c(this.a.limit(j));
    }

    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return c(this.a.I(B.d(doubleUnaryOperator)));
    }

    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return m0.c(this.a.n0(CLASSNAMEz.b(doubleToIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return n0.c(this.a.x(A.b(doubleToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return o0.c(this.a.J(CLASSNAMEw.b(doubleFunction)));
    }

    public /* synthetic */ OptionalDouble max() {
        return A.b(this.a.max());
    }

    public /* synthetic */ OptionalDouble min() {
        return A.b(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.a.K(CLASSNAMEx.b(doublePredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return j0.c(this.a.onClose(runnable));
    }

    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return c(this.a.g(CLASSNAMEu.a(doubleConsumer)));
    }

    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.a.H(d, CLASSNAMEt.b(doubleBinaryOperator));
    }

    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return A.b(this.a.D(CLASSNAMEt.b(doubleBinaryOperator)));
    }

    public /* synthetic */ DoubleStream skip(long j) {
        return c(this.a.skip(j));
    }

    public /* synthetic */ DoubleStream sorted() {
        return c(this.a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    public /* synthetic */ DoubleSummaryStatistics summaryStatistics() {
        return r.a(this.a.summaryStatistics());
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return j0.c(this.a.unordered());
    }
}
