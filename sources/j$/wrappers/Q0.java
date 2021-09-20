package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.stream.CLASSNAMEf1;
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
    final /* synthetic */ CLASSNAMEf1 a;

    private /* synthetic */ Q0(CLASSNAMEf1 f1Var) {
        this.a = f1Var;
    }

    public static /* synthetic */ LongStream n0(CLASSNAMEf1 f1Var) {
        if (f1Var == null) {
            return null;
        }
        return f1Var instanceof P0 ? ((P0) f1Var).a : new Q0(f1Var);
    }

    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.a.L(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.a.k(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.n0(this.a.asDoubleStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.a.f0(z0.a(supplier), v0.a(objLongConsumer), CLASSNAMEq.a(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ LongStream distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return n0(this.a.u(CLASSNAMEj0.a(longPredicate)));
    }

    public /* synthetic */ OptionalLong findAny() {
        return CLASSNAMEa.w(this.a.findAny());
    }

    public /* synthetic */ OptionalLong findFirst() {
        return CLASSNAMEa.w(this.a.findFirst());
    }

    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return n0(this.a.s(CLASSNAMEh0.a(longFunction)));
    }

    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
    }

    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.a.Z(CLASSNAMEf0.b(longConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ LongStream limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return n0(this.a.z(CLASSNAMEp0.c(longUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return M0.n0(this.a.O(CLASSNAMEl0.b(longToDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return O0.n0(this.a.e0(CLASSNAMEn0.b(longToIntFunction)));
    }

    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return R0.n0(this.a.Q(CLASSNAMEh0.a(longFunction)));
    }

    public /* synthetic */ OptionalLong max() {
        return CLASSNAMEa.w(this.a.max());
    }

    public /* synthetic */ OptionalLong min() {
        return CLASSNAMEa.w(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.a.S(CLASSNAMEj0.a(longPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return n0(this.a.p(CLASSNAMEf0.b(longConsumer)));
    }

    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.a.D(j, CLASSNAMEd0.a(longBinaryOperator));
    }

    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return CLASSNAMEa.w(this.a.g(CLASSNAMEd0.a(longBinaryOperator)));
    }

    public /* synthetic */ LongStream skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ LongStream sorted() {
        return n0(this.a.sorted());
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
        return I0.n0(this.a.unordered());
    }
}
