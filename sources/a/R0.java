package a;

import j$.util.k;
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

public final /* synthetic */ class R0 implements IntStream {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C1 var_a;

    private /* synthetic */ R0(C1 c1) {
        this.var_a = c1;
    }

    public static /* synthetic */ IntStream m0(C1 c1) {
        if (c1 == null) {
            return null;
        }
        return c1 instanceof Q0 ? ((Q0) c1).var_a : new R0(c1);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.var_a.L(Y.a(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.var_a.b(Y.a(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return P0.m0(this.var_a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return T0.m0(this.var_a.asLongStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.var_a.j0(C0.a(supplier), w0.a(objIntConsumer), CLASSNAMEt.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.var_a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return m0(this.var_a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return m0(this.var_a.X(Y.a(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return k.p(this.var_a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return k.p(this.var_a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return m0(this.var_a.M(W.a(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.var_a.Q(U.b(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.var_a.E(U.b(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return m0(this.var_a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return m0(this.var_a.y(CLASSNAMEe0.b(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return P0.m0(this.var_a.i0(CLASSNAMEa0.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return T0.m0(this.var_a.h(CLASSNAMEc0.a(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return V0.m0(this.var_a.F(W.a(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return k.p(this.var_a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return k.p(this.var_a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.var_a.R(Y.a(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return L0.m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return m0(this.var_a.a0(U.b(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.var_a.K(i, S.a(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return k.p(this.var_a.Z(S.a(intBinaryOperator)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return m0(this.var_a.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return m0(this.var_a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.var_a.sum();
    }

    public IntSummaryStatistics summaryStatistics() {
        this.var_a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.IntSummaryStatistics");
    }

    public /* synthetic */ int[] toArray() {
        return this.var_a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return L0.m0(this.var_a.unordered());
    }
}
