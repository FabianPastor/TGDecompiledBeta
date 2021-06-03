package j$;

import j$.time.a;
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

public final /* synthetic */ class R0 implements Stream {
    final /* synthetic */ j$.util.stream.Stream a;

    private /* synthetic */ R0(j$.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream m0(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof C$r8$wrapper$java$util$stream$Stream$VWRP ? ((C$r8$wrapper$java$util$stream$Stream$VWRP) stream).a : new R0(stream);
    }

    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.a.U(x0.c(predicate));
    }

    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.a.a(x0.c(predicate));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.k(z0.a(supplier), CLASSNAMEq.b(biConsumer), CLASSNAMEq.b(biConsumer2));
    }

    public /* synthetic */ Object collect(Collector collector) {
        return this.a.T(J0.a(collector));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Stream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ Stream filter(Predicate predicate) {
        return m0(this.a.P(x0.c(predicate)));
    }

    public /* synthetic */ Optional findAny() {
        return a.n(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return a.n(this.a.findFirst());
    }

    public /* synthetic */ Stream flatMap(Function function) {
        return m0(this.a.p(M.c(function)));
    }

    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return M0.m0(this.a.B(M.c(function)));
    }

    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return O0.m0(this.a.e(M.c(function)));
    }

    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return Q0.m0(this.a.V(M.c(function)));
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.a.g(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ Stream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ Stream map(Function function) {
        return m0(this.a.n(M.c(function)));
    }

    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return M0.m0(this.a.h0(B0.a(toDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return O0.m0(this.a.m(D0.a(toIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return Q0.m0(this.a.e0(F0.a(toLongFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return a.n(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return a.n(this.a.min(comparator));
    }

    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.a.c0(x0.c(predicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return I0.m0(this.a.parallel());
    }

    public /* synthetic */ Stream peek(Consumer consumer) {
        return m0(this.a.S(CLASSNAMEw.b(consumer)));
    }

    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.a.z(obj, CLASSNAMEs.b(biFunction), CLASSNAMEu.b(binaryOperator));
    }

    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.a.l0(obj, CLASSNAMEu.b(binaryOperator));
    }

    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return a.n(this.a.s(CLASSNAMEu.b(binaryOperator)));
    }

    public /* synthetic */ BaseStream sequential() {
        return I0.m0(this.a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return m0(this.a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEh.a(this.a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.a.toArray(T.a(intFunction));
    }

    public /* synthetic */ BaseStream unordered() {
        return I0.m0(this.a.unordered());
    }
}
