package a;

import j$.util.k;
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

public final /* synthetic */ class V0 implements Stream {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.stream.Stream var_a;

    private /* synthetic */ V0(j$.util.stream.Stream stream) {
        this.var_a = stream;
    }

    public static /* synthetic */ Stream m0(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof U0 ? ((U0) stream).var_a : new V0(stream);
    }

    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.var_a.U(A0.c(predicate));
    }

    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.var_a.a(A0.c(predicate));
    }

    public /* synthetic */ void close() {
        this.var_a.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.var_a.k(C0.a(supplier), CLASSNAMEt.b(biConsumer), CLASSNAMEt.b(biConsumer2));
    }

    public /* synthetic */ Object collect(Collector collector) {
        return this.var_a.T(M0.a(collector));
    }

    public /* synthetic */ long count() {
        return this.var_a.count();
    }

    public /* synthetic */ Stream distinct() {
        return m0(this.var_a.distinct());
    }

    public /* synthetic */ Stream filter(Predicate predicate) {
        return m0(this.var_a.P(A0.c(predicate)));
    }

    public /* synthetic */ Optional findAny() {
        return k.n(this.var_a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return k.n(this.var_a.findFirst());
    }

    public /* synthetic */ Stream flatMap(Function function) {
        return m0(this.var_a.p(P.c(function)));
    }

    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return P0.m0(this.var_a.B(P.c(function)));
    }

    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return R0.m0(this.var_a.e(P.c(function)));
    }

    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return T0.m0(this.var_a.V(P.c(function)));
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.var_a.forEach(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.var_a.g(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.var_a.iterator();
    }

    public /* synthetic */ Stream limit(long j) {
        return m0(this.var_a.limit(j));
    }

    public /* synthetic */ Stream map(Function function) {
        return m0(this.var_a.n(P.c(function)));
    }

    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return P0.m0(this.var_a.h0(E0.a(toDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return R0.m0(this.var_a.m(G0.a(toIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return T0.m0(this.var_a.e0(I0.a(toLongFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return k.n(this.var_a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return k.n(this.var_a.min(comparator));
    }

    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.var_a.c0(A0.c(predicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return L0.m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return L0.m0(this.var_a.parallel());
    }

    public /* synthetic */ Stream peek(Consumer consumer) {
        return m0(this.var_a.S(CLASSNAMEz.b(consumer)));
    }

    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.var_a.z(obj, CLASSNAMEv.b(biFunction), CLASSNAMEx.b(binaryOperator));
    }

    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.var_a.l0(obj, CLASSNAMEx.b(binaryOperator));
    }

    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return k.n(this.var_a.s(CLASSNAMEx.b(binaryOperator)));
    }

    public /* synthetic */ BaseStream sequential() {
        return L0.m0(this.var_a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return m0(this.var_a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return m0(this.var_a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return m0(this.var_a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEk.a(this.var_a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.var_a.toArray();
    }

    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.var_a.toArray(W.a(intFunction));
    }

    public /* synthetic */ BaseStream unordered() {
        return L0.m0(this.var_a.unordered());
    }
}
