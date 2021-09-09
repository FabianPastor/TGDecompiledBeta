package j$.wrappers;

import j$.util.CLASSNAMEa;
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

    public static /* synthetic */ Stream n0(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof C$r8$wrapper$java$util$stream$Stream$VWRP ? ((C$r8$wrapper$java$util$stream$Stream$VWRP) stream).a : new R0(stream);
    }

    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.a.W(x0.c(predicate));
    }

    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.a.a(x0.c(predicate));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.i(z0.a(supplier), CLASSNAMEq.a(biConsumer), CLASSNAMEq.a(biConsumer2));
    }

    public /* synthetic */ Object collect(Collector collector) {
        return this.a.b0(J0.d(collector));
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Stream distinct() {
        return n0(this.a.distinct());
    }

    public /* synthetic */ Stream filter(Predicate predicate) {
        return n0(this.a.T(x0.c(predicate)));
    }

    public /* synthetic */ Optional findAny() {
        return CLASSNAMEa.t(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return CLASSNAMEa.t(this.a.findFirst());
    }

    public /* synthetic */ Stream flatMap(Function function) {
        return n0(this.a.o(M.a(function)));
    }

    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return M0.n0(this.a.E(M.a(function)));
    }

    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return O0.n0(this.a.c(M.a(function)));
    }

    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return Q0.n0(this.a.X(M.a(function)));
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.a.e(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ Stream limit(long j) {
        return n0(this.a.limit(j));
    }

    public /* synthetic */ Stream map(Function function) {
        return n0(this.a.n(M.a(function)));
    }

    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return M0.n0(this.a.j0(B0.a(toDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return O0.n0(this.a.m(D0.a(toIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return Q0.n0(this.a.g0(F0.a(toLongFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return CLASSNAMEa.t(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return CLASSNAMEa.t(this.a.min(comparator));
    }

    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.a.d0(x0.c(predicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return I0.n0(this.a.parallel());
    }

    public /* synthetic */ Stream peek(Consumer consumer) {
        return n0(this.a.V(CLASSNAMEw.b(consumer)));
    }

    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.a.B(obj, CLASSNAMEs.a(biFunction), CLASSNAMEu.a(binaryOperator));
    }

    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.a.m0(obj, CLASSNAMEu.a(binaryOperator));
    }

    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return CLASSNAMEa.t(this.a.t(CLASSNAMEu.a(binaryOperator)));
    }

    public /* synthetic */ BaseStream sequential() {
        return I0.n0(this.a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return n0(this.a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return n0(this.a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return n0(this.a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEh.a(this.a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.a.l(T.a(intFunction));
    }

    public /* synthetic */ BaseStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
