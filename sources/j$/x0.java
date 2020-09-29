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

public final /* synthetic */ class x0 implements IntStream {
    final /* synthetic */ A2 a;

    private /* synthetic */ x0(A2 a2) {
        this.a = a2;
    }

    public static /* synthetic */ IntStream c(A2 a2) {
        if (a2 == null) {
            return null;
        }
        return new x0(a2);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.M(V.b(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.b(V.b(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return w0.c(this.a.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return y0.c(this.a.asLongStream());
    }

    public /* synthetic */ OptionalDouble average() {
        return A.b(this.a.average());
    }

    public /* synthetic */ Stream boxed() {
        return z0.c(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.l0(q0.a(supplier), m0.b(objIntConsumer), CLASSNAMEy.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ IntStream distinct() {
        return c(this.a.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return c(this.a.a0(V.b(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return A.c(this.a.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return A.c(this.a.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return c(this.a.N(U.b(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.S(S.a(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.F(S.a(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return c(this.a.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return c(this.a.z(Z.d(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return w0.c(this.a.V(X.b(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return y0.c(this.a.k(Y.b(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return z0.c(this.a.G(U.b(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return A.c(this.a.max());
    }

    public /* synthetic */ OptionalInt min() {
        return A.c(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.T(V.b(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return u0.c(this.a.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return c(this.a.d0(S.a(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.L(i, Q.b(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return A.c(this.a.c0(Q.b(intBinaryOperator)));
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
        return u0.c(this.a.unordered());
    }
}
