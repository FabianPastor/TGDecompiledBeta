package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Spliterator;
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
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$IntStream$-WRP  reason: invalid class name */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$IntStream$WRP implements IntStream {
    final /* synthetic */ j$.util.stream.IntStream a;

    private /* synthetic */ C$r8$wrapper$java$util$stream$IntStream$WRP(j$.util.stream.IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ IntStream convert(j$.util.stream.IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof C$r8$wrapper$java$util$stream$IntStream$VWRP ? ((C$r8$wrapper$java$util$stream$IntStream$VWRP) intStream).a : new C$r8$wrapper$java$util$stream$IntStream$WRP(intStream);
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.a.C(V.a(intPredicate));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.a.F(V.a(intPredicate));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.n0(this.a.asDoubleStream());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ LongStream asLongStream() {
        return O0.n0(this.a.asLongStream());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalDouble average() {
        return AbstractCLASSNAMEa.u(this.a.average());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ Stream boxed() {
        return P0.n0(this.a.boxed());
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.a.k0(z0.a(supplier), t0.a(objIntConsumer), CLASSNAMEq.a(biConsumer));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream distinct() {
        return convert(this.a.distinct());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return convert(this.a.h(V.a(intPredicate)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalInt findAny() {
        return AbstractCLASSNAMEa.v(this.a.findAny());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalInt findFirst() {
        return AbstractCLASSNAMEa.v(this.a.findFirst());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return convert(this.a.P(T.a(intFunction)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.a.U(Q.b(intConsumer));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.a.I(Q.b(intConsumer));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    public /* synthetic */ Iterator<Integer> iterator() {
        return this.a.moNUMiterator();
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    /* renamed from: iterator  reason: avoid collision after fix types in other method */
    public /* synthetic */ Iterator<Integer> iterator2() {
        return CLASSNAMEd.a(this.a.moNUMiterator());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream limit(long j) {
        return convert(this.a.limit(j));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return convert(this.a.q(CLASSNAMEb0.b(intUnaryOperator)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return M0.n0(this.a.A(X.b(intToDoubleFunction)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return O0.n0(this.a.f(Z.a(intToLongFunction)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return P0.n0(this.a.J(T.a(intFunction)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalInt max() {
        return AbstractCLASSNAMEa.v(this.a.max());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalInt min() {
        return AbstractCLASSNAMEa.v(this.a.min());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.a.v(V.a(intPredicate));
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ IntStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    public /* synthetic */ IntStream parallel() {
        return I0.n0(this.a.moNUMparallel());
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    /* renamed from: parallel  reason: avoid collision after fix types in other method */
    public /* synthetic */ IntStream parallel2() {
        return convert(this.a.moNUMparallel());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return convert(this.a.c0(Q.b(intConsumer)));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.a.N(i, O.a(intBinaryOperator));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return AbstractCLASSNAMEa.v(this.a.a0(O.a(intBinaryOperator)));
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    public /* synthetic */ IntStream sequential() {
        return I0.n0(this.a.moNUMsequential());
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    /* renamed from: sequential  reason: avoid collision after fix types in other method */
    public /* synthetic */ IntStream sequential2() {
        return convert(this.a.moNUMsequential());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream skip(long j) {
        return convert(this.a.skip(j));
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ IntStream sorted() {
        return convert(this.a.sorted());
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    public /* synthetic */ Spliterator<Integer> spliterator() {
        return CLASSNAMEl.a(this.a.moNUMspliterator());
    }

    @Override // java.util.stream.IntStream, java.util.stream.BaseStream
    /* renamed from: spliterator  reason: avoid collision after fix types in other method */
    public /* synthetic */ Spliterator<Integer> spliterator2() {
        return CLASSNAMEh.a(this.a.moNUMspliterator());
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    @Override // java.util.stream.IntStream
    public IntSummaryStatistics summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.IntSummaryStatistics");
    }

    @Override // java.util.stream.IntStream
    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ IntStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
