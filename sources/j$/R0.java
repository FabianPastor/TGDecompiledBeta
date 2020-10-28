package j$;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEq;
import j$.util.stream.CLASSNAMEx2;
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
    final /* synthetic */ CLASSNAMEx2 a;

    private /* synthetic */ R0(CLASSNAMEx2 x2Var) {
        this.a = x2Var;
    }

    public static /* synthetic */ IntStream m0(CLASSNAMEx2 x2Var) {
        if (x2Var == null) {
            return null;
        }
        return x2Var instanceof Q0 ? ((Q0) x2Var).a : new R0(x2Var);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.L(Y.a(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.e0(Y.a(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return P0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return T0.m0(this.a.asLongStream());
    }

    public /* synthetic */ OptionalDouble average() {
        return CLASSNAMEk.o(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return V0.m0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.j0(C0.a(supplier), w0.a(objIntConsumer), CLASSNAMEt.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return m0(this.a.Z(Y.a(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return CLASSNAMEk.p(this.a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return CLASSNAMEk.p(this.a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return m0(this.a.M(W.a(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.Q(U.b(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.E(U.b(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return m0(this.a.W(CLASSNAMEe0.b(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return P0.m0(this.a.g0(CLASSNAMEa0.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return T0.m0(this.a.g(CLASSNAMEc0.a(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return V0.m0(this.a.F(W.a(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return CLASSNAMEk.p(this.a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return CLASSNAMEk.p(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.i0(Y.a(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return L0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return m0(this.a.a0(U.b(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.K(i, S.a(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return CLASSNAMEk.p(this.a.Y(S.a(intBinaryOperator)));
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

    public /* synthetic */ IntSummaryStatistics summaryStatistics() {
        return CLASSNAMEq.b(this.a.summaryStatistics());
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return L0.m0(this.a.unordered());
    }
}
