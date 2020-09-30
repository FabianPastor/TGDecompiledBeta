package j$;

import j$.util.A;
import j$.util.CLASSNAMEx;
import j$.util.stream.W2;
import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final /* synthetic */ class n0 implements LongStream {
    final /* synthetic */ W2 a;

    private /* synthetic */ n0(W2 w2) {
        this.a = w2;
    }

    public static /* synthetic */ LongStream c(W2 w2) {
        if (w2 == null) {
            return null;
        }
        return new n0(w2);
    }

    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.a.i0(U.b(longPredicate));
    }

    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.a.e0(U.b(longPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return l0.c(this.a.asDoubleStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.a.g0(f0.a(supplier), c0.b(objLongConsumer), CLASSNAMEn.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ LongStream distinct() {
        return c(this.a.distinct());
    }

    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return c(this.a.j0(U.b(longPredicate)));
    }

    public /* synthetic */ OptionalLong findAny() {
        return A.d(this.a.findAny());
    }

    public /* synthetic */ OptionalLong findFirst() {
        return A.d(this.a.findFirst());
    }

    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return c(this.a.t(T.b(longFunction)));
    }

    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.a.i(Q.a(longConsumer));
    }

    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.a.b0(Q.a(longConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ LongStream limit(long j) {
        return c(this.a.limit(j));
    }

    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return c(this.a.y(Y.c(longUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return l0.c(this.a.q(W.b(longToDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return m0.c(this.a.O(X.b(longToIntFunction)));
    }

    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return o0.c(this.a.P(T.b(longFunction)));
    }

    public /* synthetic */ OptionalLong max() {
        return A.d(this.a.max());
    }

    public /* synthetic */ OptionalLong min() {
        return A.d(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.a.f(U.b(longPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return j0.c(this.a.onClose(runnable));
    }

    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return c(this.a.s(Q.a(longConsumer)));
    }

    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.a.B(j, P.b(longBinaryOperator));
    }

    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return A.d(this.a.l(P.b(longBinaryOperator)));
    }

    public /* synthetic */ LongStream skip(long j) {
        return c(this.a.skip(j));
    }

    public /* synthetic */ LongStream sorted() {
        return c(this.a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    public /* synthetic */ LongSummaryStatistics summaryStatistics() {
        return CLASSNAMEx.a(this.a.summaryStatistics());
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return j0.c(this.a.unordered());
    }
}
