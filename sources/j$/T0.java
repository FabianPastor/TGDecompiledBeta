package j$;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEs;
import j$.util.stream.T2;
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

public final /* synthetic */ class T0 implements LongStream {
    final /* synthetic */ T2 a;

    private /* synthetic */ T0(T2 t2) {
        this.a = t2;
    }

    public static /* synthetic */ LongStream m0(T2 t2) {
        if (t2 == null) {
            return null;
        }
        return t2 instanceof S0 ? ((S0) t2).a : new T0(t2);
    }

    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.a.y(CLASSNAMEm0.a(longPredicate));
    }

    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.a.l(CLASSNAMEm0.a(longPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return P0.m0(this.a.asDoubleStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.a.c0(C0.a(supplier), y0.a(objLongConsumer), CLASSNAMEt.b(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ LongStream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return m0(this.a.G(CLASSNAMEm0.a(longPredicate)));
    }

    public /* synthetic */ OptionalLong findAny() {
        return CLASSNAMEk.q(this.a.findAny());
    }

    public /* synthetic */ OptionalLong findFirst() {
        return CLASSNAMEk.q(this.a.findFirst());
    }

    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return m0(this.a.s(CLASSNAMEk0.a(longFunction)));
    }

    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.a.e(CLASSNAMEi0.b(longConsumer));
    }

    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.a.X(CLASSNAMEi0.b(longConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ LongStream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return m0(this.a.x(CLASSNAMEs0.c(longUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return P0.m0(this.a.i(CLASSNAMEo0.b(longToDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return R0.m0(this.a.w(CLASSNAMEq0.b(longToIntFunction)));
    }

    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return V0.m0(this.a.N(CLASSNAMEk0.a(longFunction)));
    }

    public /* synthetic */ OptionalLong max() {
        return CLASSNAMEk.q(this.a.max());
    }

    public /* synthetic */ OptionalLong min() {
        return CLASSNAMEk.q(this.a.min());
    }

    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.a.r(CLASSNAMEm0.a(longPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return L0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return m0(this.a.q(CLASSNAMEi0.b(longConsumer)));
    }

    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.a.A(j, CLASSNAMEg0.a(longBinaryOperator));
    }

    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return CLASSNAMEk.q(this.a.h(CLASSNAMEg0.a(longBinaryOperator)));
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

    public /* synthetic */ LongSummaryStatistics summaryStatistics() {
        return CLASSNAMEs.b(this.a.summaryStatistics());
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return L0.m0(this.a.unordered());
    }
}
