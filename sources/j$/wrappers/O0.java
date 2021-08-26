package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.stream.M0;
import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final /* synthetic */ class O0 implements IntStream {
    final /* synthetic */ M0 a;

    private /* synthetic */ O0(M0 m0) {
        this.a = m0;
    }

    public static /* synthetic */ IntStream n0(M0 m0) {
        if (m0 == null) {
            return null;
        }
        return m0 instanceof N0 ? ((N0) m0).a : new O0(m0);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.C(V.a(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.F(V.a(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.n0(this.a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return Q0.n0(this.a.asLongStream());
    }

    public /* synthetic */ OptionalDouble average() {
        return CLASSNAMEa.u(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return R0.n0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.k0(z0.a(supplier), t0.a(objIntConsumer), CLASSNAMEq.a(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return n0(this.a.h(V.a(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return CLASSNAMEa.v(this.a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return CLASSNAMEa.v(this.a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return n0(this.a.P(T.a(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.U(Q.b(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.I(Q.b(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return n0(this.a.q(CLASSNAMEb0.b(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return M0.n0(this.a.A(X.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return Q0.n0(this.a.f(Z.a(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return R0.n0(this.a.J(T.a(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return CLASSNAMEa.v(this.a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return CLASSNAMEa.v(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.v(V.a(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return n0(this.a.c0(Q.b(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.N(i, O.a(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return CLASSNAMEa.v(this.a.a0(O.a(intBinaryOperator)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return n0(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public IntSummaryStatistics summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.IntSummaryStatistics");
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
