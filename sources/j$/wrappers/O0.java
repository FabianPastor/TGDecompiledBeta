package j$.wrappers;

import j$.util.AbstractCLASSNAMEa;
import j$.util.stream.InterfaceCLASSNAMEe1;
import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Spliterator;
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
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
/* loaded from: classes2.dex */
public final /* synthetic */ class O0 implements LongStream {
    final /* synthetic */ InterfaceCLASSNAMEe1 a;

    private /* synthetic */ O0(InterfaceCLASSNAMEe1 interfaceCLASSNAMEe1) {
        this.a = interfaceCLASSNAMEe1;
    }

    public static /* synthetic */ LongStream n0(InterfaceCLASSNAMEe1 interfaceCLASSNAMEe1) {
        if (interfaceCLASSNAMEe1 == null) {
            return null;
        }
        return interfaceCLASSNAMEe1 instanceof N0 ? ((N0) interfaceCLASSNAMEe1).a : new O0(interfaceCLASSNAMEe1);
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.a.L(CLASSNAMEj0.a(longPredicate));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.a.k(CLASSNAMEj0.a(longPredicate));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ DoubleStream asDoubleStream() {
        return M0.n0(this.a.asDoubleStream());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalDouble average() {
        return AbstractCLASSNAMEa.u(this.a.average());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Stream boxed() {
        return P0.n0(this.a.boxed());
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.a.f0(z0.a(supplier), v0.a(objLongConsumer), CLASSNAMEq.a(biConsumer));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream distinct() {
        return n0(this.a.distinct());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return n0(this.a.u(CLASSNAMEj0.a(longPredicate)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong findAny() {
        return AbstractCLASSNAMEa.w(this.a.findAny());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong findFirst() {
        return AbstractCLASSNAMEa.w(this.a.findFirst());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return n0(this.a.s(CLASSNAMEh0.a(longFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.a.Z(CLASSNAMEf0.b(longConsumer));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    public /* synthetic */ Iterator<Long> iterator() {
        return this.a.mo307iterator();
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    /* renamed from: iterator */
    public /* synthetic */ Iterator<Long> iterator2() {
        return CLASSNAMEf.a(this.a.mo307iterator());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return n0(this.a.z(CLASSNAMEp0.c(longUnaryOperator)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return M0.n0(this.a.O(CLASSNAMEl0.b(longToDoubleFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.a.e0(CLASSNAMEn0.b(longToIntFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return P0.n0(this.a.Q(CLASSNAMEh0.a(longFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong max() {
        return AbstractCLASSNAMEa.w(this.a.max());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong min() {
        return AbstractCLASSNAMEa.w(this.a.min());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.a.S(CLASSNAMEj0.a(longPredicate));
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ LongStream onClose(Runnable runnable) {
        return I0.n0(this.a.onClose(runnable));
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    public /* synthetic */ LongStream parallel() {
        return I0.n0(this.a.mo308parallel());
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    /* renamed from: parallel */
    public /* synthetic */ LongStream parallel2() {
        return n0(this.a.mo308parallel());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return n0(this.a.p(CLASSNAMEf0.b(longConsumer)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.a.D(j, CLASSNAMEd0.a(longBinaryOperator));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return AbstractCLASSNAMEa.w(this.a.g(CLASSNAMEd0.a(longBinaryOperator)));
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    public /* synthetic */ LongStream sequential() {
        return I0.n0(this.a.mo309sequential());
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    /* renamed from: sequential */
    public /* synthetic */ LongStream sequential2() {
        return n0(this.a.mo309sequential());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream sorted() {
        return n0(this.a.sorted());
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    public /* synthetic */ Spliterator<Long> spliterator() {
        return CLASSNAMEn.a(this.a.mo310spliterator());
    }

    @Override // java.util.stream.LongStream, java.util.stream.BaseStream
    /* renamed from: spliterator */
    public /* synthetic */ Spliterator<Long> spliterator2() {
        return CLASSNAMEh.a(this.a.mo310spliterator());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    @Override // java.util.stream.LongStream
    public LongSummaryStatistics summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.LongSummaryStatistics");
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ LongStream unordered() {
        return I0.n0(this.a.unordered());
    }
}
