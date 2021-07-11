package j$;

import j$.time.a;
import j$.util.stream.H1;
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

public final /* synthetic */ class Q0 implements LongStream {
    final /* synthetic */ H1 a;

    private /* synthetic */ Q0(H1 h1) {
        this.a = h1;
    }

    public static /* synthetic */ LongStream m0(H1 h1) {
        if (h1 == null) {
            return null;
        }
        return h1 instanceof P0 ? ((P0) h1).a : new Q0(h1);
    }

    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.a.f0(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.a.b0(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.m0(this.a.asDoubleStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.a.d0(z0.a(supplier), v0.a(objLongConsumer), CLASSNAMEq.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ LongStream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return m0(this.a.g0(CLASSNAMEj0.a(longPredicate)));
    }

    public /* synthetic */ OptionalLong findAny() {
        return a.q(this.a.findAny());
    }

    public /* synthetic */ OptionalLong findFirst() {
        return a.q(this.a.findFirst());
    }

    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return m0(this.a.r(CLASSNAMEh0.a(longFunction)));
    }

    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.a.f(CLASSNAMEf0.b(longConsumer));
    }

    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.a.Y(CLASSNAMEf0.b(longConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ LongStream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return m0(this.a.x(CLASSNAMEp0.c(longUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return M0.m0(this.a.j(CLASSNAMEl0.b(longToDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return O0.m0(this.a.w(CLASSNAMEn0.b(longToIntFunction)));
    }

    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return R0.m0(this.a.N(CLASSNAMEh0.a(longFunction)));
    }

    public /* synthetic */ OptionalLong max() {
        return a.q(this.a.max());
    }

    public /* synthetic */ OptionalLong min() {
        return a.q(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.a.c(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return m0(this.a.q(CLASSNAMEf0.b(longConsumer)));
    }

    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.a.A(j, CLASSNAMEd0.a(longBinaryOperator));
    }

    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return a.q(this.a.i(CLASSNAMEd0.a(longBinaryOperator)));
    }

    public /* synthetic */ LongStream skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ LongStream sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    public LongSummaryStatistics summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.LongSummaryStatistics");
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return I0.m0(this.a.unordered());
    }
}
