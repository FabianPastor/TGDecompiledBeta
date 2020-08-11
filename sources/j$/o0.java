package j$;

import j$.util.A;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.BaseStream;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final /* synthetic */ class o0 implements Stream {
    final /* synthetic */ j$.util.stream.Stream a;

    private /* synthetic */ o0(j$.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream c(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return new o0(stream);
    }

    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.a.X(d0.c(predicate));
    }

    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.a.a(d0.c(predicate));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.m(f0.a(supplier), CLASSNAMEn.b(biConsumer), CLASSNAMEn.b(biConsumer2));
    }

    public /* synthetic */ Object collect(Collector collector) {
        return this.a.W(k0.e(collector));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Stream distinct() {
        return c(this.a.distinct());
    }

    public /* synthetic */ Stream filter(Predicate predicate) {
        return c(this.a.R(d0.c(predicate)));
    }

    public /* synthetic */ Optional findAny() {
        return A.a(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return A.a(this.a.findFirst());
    }

    public /* synthetic */ Stream flatMap(Function function) {
        return c(this.a.r(D.c(function)));
    }

    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return l0.c(this.a.C(D.c(function)));
    }

    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return m0.c(this.a.h(D.c(function)));
    }

    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return n0.c(this.a.Y(D.c(function)));
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(r.a(consumer));
    }

    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.a.j(r.a(consumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ Stream limit(long j) {
        return c(this.a.limit(j));
    }

    public /* synthetic */ Stream map(Function function) {
        return c(this.a.p(D.c(function)));
    }

    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return l0.c(this.a.k0(g0.b(toDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return m0.c(this.a.o(h0.b(toIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return n0.c(this.a.h0(i0.b(toLongFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return A.a(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return A.a(this.a.min(comparator));
    }

    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.a.f0(d0.c(predicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return j0.c(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return j0.c(this.a.parallel());
    }

    public /* synthetic */ Stream peek(Consumer consumer) {
        return c(this.a.U(r.a(consumer)));
    }

    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.a.A(obj, CLASSNAMEp.b(biFunction), CLASSNAMEq.b(binaryOperator));
    }

    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.a.o0(obj, CLASSNAMEq.b(binaryOperator));
    }

    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return A.a(this.a.u(CLASSNAMEq.b(binaryOperator)));
    }

    public /* synthetic */ BaseStream sequential() {
        return j0.c(this.a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return c(this.a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return c(this.a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return c(this.a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEi.a(this.a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.a.toArray(I.b(intFunction));
    }

    public /* synthetic */ BaseStream unordered() {
        return j0.c(this.a.unordered());
    }
}
