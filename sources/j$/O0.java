package j$;

import j$.time.a;
import j$.util.stream.C1;
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
    final /* synthetic */ C1 a;

    private /* synthetic */ O0(C1 c1) {
        this.a = c1;
    }

    public static /* synthetic */ IntStream m0(C1 c1) {
        if (c1 == null) {
            return null;
        }
        return c1 instanceof N0 ? ((N0) c1).a : new O0(c1);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.L(V.a(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.b(V.a(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return Q0.m0(this.a.asLongStream());
    }

    public /* synthetic */ OptionalDouble average() {
        return a.o(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return R0.m0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.j0(z0.a(supplier), t0.a(objIntConsumer), CLASSNAMEq.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return m0(this.a.X(V.a(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return a.p(this.a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return a.p(this.a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return m0(this.a.M(T.a(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.Q(Q.b(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.E(Q.b(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return m0(this.a.y(CLASSNAMEb0.b(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return M0.m0(this.a.i0(X.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return Q0.m0(this.a.h(Z.a(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return R0.m0(this.a.F(T.a(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return a.p(this.a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return a.p(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.R(V.a(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return m0(this.a.a0(Q.b(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.K(i, O.a(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return a.p(this.a.Z(O.a(intBinaryOperator)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return m0(this.a.sorted());
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
        return I0.m0(this.a.unordered());
    }
}
