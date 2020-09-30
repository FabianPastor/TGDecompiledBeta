package j$;

import j$.util.A;
import j$.util.CLASSNAMEt;
import j$.util.stream.A2;
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

public final /* synthetic */ class m0 implements IntStream {
    final /* synthetic */ A2 a;

    private /* synthetic */ m0(A2 a2) {
        this.a = a2;
    }

    public static /* synthetic */ IntStream c(A2 a2) {
        if (a2 == null) {
            return null;
        }
        return new m0(a2);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.M(J.b(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.b(J.b(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return l0.c(this.a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return n0.c(this.a.asLongStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.l0(f0.a(supplier), b0.b(objIntConsumer), CLASSNAMEn.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return c(this.a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return c(this.a.a0(J.b(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return A.c(this.a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return A.c(this.a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return c(this.a.N(I.b(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.S(G.a(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.F(G.a(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return c(this.a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return c(this.a.z(N.d(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return l0.c(this.a.V(L.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return n0.c(this.a.k(M.b(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return o0.c(this.a.G(I.b(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return A.c(this.a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return A.c(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.T(J.b(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return j0.c(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return c(this.a.d0(G.a(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.L(i, F.b(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return A.c(this.a.c0(F.b(intBinaryOperator)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return c(this.a.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return c(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public /* synthetic */ IntSummaryStatistics summaryStatistics() {
        return CLASSNAMEt.a(this.a.summaryStatistics());
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return j0.c(this.a.unordered());
    }
}
