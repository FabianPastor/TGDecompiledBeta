package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.stream.U;
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

public final /* synthetic */ class M0 implements DoubleStream {
    final /* synthetic */ U a;

    private /* synthetic */ M0(U u) {
        this.a = u;
    }

    public static /* synthetic */ DoubleStream n0(U u) {
        if (u == null) {
            return null;
        }
        return u instanceof L0 ? ((L0) u).a : new M0(u);
    }

    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.a.Y(E.a(doublePredicate));
    }

    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.a.h0(E.a(doublePredicate));
    }

    public /* synthetic */ OptionalDouble average() {
        return CLASSNAMEa.u(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return P0.n0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.a.H(z0.a(supplier), r0.a(objDoubleConsumer), CLASSNAMEq.a(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ DoubleStream distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return n0(this.a.r(E.a(doublePredicate)));
    }

    public /* synthetic */ OptionalDouble findAny() {
        return CLASSNAMEa.u(this.a.findAny());
    }

    public /* synthetic */ OptionalDouble findFirst() {
        return CLASSNAMEa.u(this.a.findFirst());
    }

    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return n0(this.a.w(C.a(doubleFunction)));
    }

    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.a.j(A.b(doubleConsumer));
    }

    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.a.l0(A.b(doubleConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ DoubleStream limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return n0(this.a.y(K.b(doubleUnaryOperator)));
    }

    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.a.R(G.b(doubleToIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return O0.n0(this.a.x(I.a(doubleToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return P0.n0(this.a.M(C.a(doubleFunction)));
    }

    public /* synthetic */ OptionalDouble max() {
        return CLASSNAMEa.u(this.a.max());
    }

    public /* synthetic */ OptionalDouble min() {
        return CLASSNAMEa.u(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.a.i0(E.a(doublePredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return n0(this.a.b(A.b(doubleConsumer)));
    }

    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.a.K(d, CLASSNAMEy.a(doubleBinaryOperator));
    }

    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return CLASSNAMEa.u(this.a.G(CLASSNAMEy.a(doubleBinaryOperator)));
    }

    public /* synthetic */ DoubleStream skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ DoubleStream sorted() {
        return n0(this.a.sorted());
    }

    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    public DoubleSummaryStatistics summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.DoubleSummaryStatistics");
    }

    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
