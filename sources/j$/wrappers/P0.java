package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
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
/* loaded from: classes2.dex */
public final /* synthetic */ class P0 implements Stream {
    final /* synthetic */ j$.util.stream.Stream a;

    private /* synthetic */ P0(j$.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream n0(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof C$r8$wrapper$java$util$stream$Stream$VWRP ? ((C$r8$wrapper$java$util$stream$Stream$VWRP) stream).a : new P0(stream);
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.a.W(x0.a(predicate));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.a.a(x0.a(predicate));
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.i(z0.a(supplier), CLASSNAMEq.a(biConsumer), CLASSNAMEq.a(biConsumer2));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object collect(Collector collector) {
        return this.a.b0(J0.d(collector));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream distinct() {
        return n0(this.a.distinct());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream filter(Predicate predicate) {
        return n0(this.a.T(x0.a(predicate)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Optional findAny() {
        return AbstractCLASSNAMEa.t(this.a.findAny());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Optional findFirst() {
        return AbstractCLASSNAMEa.t(this.a.findFirst());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream flatMap(Function function) {
        return n0(this.a.o(M.a(function)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return M0.n0(this.a.E(M.a(function)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.a.c(M.a(function)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return O0.n0(this.a.X(M.a(function)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.a.e(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Iterator iterator() {
        return this.a.mo307iterator();
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream map(Function function) {
        return n0(this.a.n(M.a(function)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return M0.n0(this.a.j0(B0.a(toDoubleFunction)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.a.m(D0.a(toIntFunction)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return O0.n0(this.a.g0(F0.a(toLongFunction)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Optional max(Comparator comparator) {
        return AbstractCLASSNAMEa.t(this.a.max(comparator));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Optional min(Comparator comparator) {
        return AbstractCLASSNAMEa.t(this.a.min(comparator));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.a.d0(x0.a(predicate));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream parallel() {
        return I0.n0(this.a.mo308parallel());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream peek(Consumer consumer) {
        return n0(this.a.V(CLASSNAMEw.b(consumer)));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.a.B(obj, CLASSNAMEs.a(biFunction), CLASSNAMEu.a(binaryOperator));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.a.m0(obj, CLASSNAMEu.a(binaryOperator));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return AbstractCLASSNAMEa.t(this.a.t(CLASSNAMEu.a(binaryOperator)));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream sequential() {
        return I0.n0(this.a.mo309sequential());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream sorted() {
        return n0(this.a.sorted());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Stream sorted(Comparator comparator) {
        return n0(this.a.sorted(comparator));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEh.a(this.a.mo310spliterator());
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    @Override // java.util.stream.Stream
    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.a.l(T.a(intFunction));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
