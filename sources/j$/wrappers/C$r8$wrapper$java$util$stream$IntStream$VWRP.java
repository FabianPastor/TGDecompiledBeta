package j$.wrappers;

import j$.util.IntSummaryStatistics;
import j$.util.IntSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.function.BiConsumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntPredicate;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.function.ObjIntConsumer;
import j$.util.function.Supplier;
import j$.util.stream.BaseStream;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$IntStream$-V-WRP  reason: invalid class name */
/* compiled from: IntStream */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$IntStream$VWRP implements IntStream {
    final /* synthetic */ java.util.stream.IntStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$IntStream$VWRP(java.util.stream.IntStream intStream) {
        this.wrappedValue = intStream;
    }

    public static /* synthetic */ IntStream convert(java.util.stream.IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof C$r8$wrapper$java$util$stream$IntStream$WRP ? ((C$r8$wrapper$java$util$stream$IntStream$WRP) intStream).wrappedValue : new C$r8$wrapper$java$util$stream$IntStream$VWRP(intStream);
    }

    public /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate));
    }

    public /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.asDoubleStream());
    }

    public /* synthetic */ LongStream asLongStream() {
        return C$r8$wrapper$java$util$stream$LongStream$VWRP.convert(this.wrappedValue.asLongStream());
    }

    public /* synthetic */ OptionalDouble average() {
        return OptionalConversions.convert(this.wrappedValue.average());
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.wrappedValue.boxed());
    }

    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$WRP.convert(supplier), C$r8$wrapper$java$util$function$ObjIntConsumer$WRP.convert(objIntConsumer), C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    public /* synthetic */ IntStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    public /* synthetic */ IntStream filter(IntPredicate intPredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate)));
    }

    public /* synthetic */ OptionalInt findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    public /* synthetic */ OptionalInt findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    public /* synthetic */ IntStream flatMap(IntFunction intFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$IntFunction$WRP.convert(intFunction)));
    }

    public /* synthetic */ void forEach(IntConsumer intConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    public /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    public /* synthetic */ IntStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    public /* synthetic */ IntStream map(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$IntUnaryOperator$WRP.convert(intUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.mapToDouble(C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP.convert(intToDoubleFunction)));
    }

    public /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$VWRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$IntToLongFunction$WRP.convert(intToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(IntFunction intFunction) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$IntFunction$WRP.convert(intFunction)));
    }

    public /* synthetic */ OptionalInt max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    public /* synthetic */ OptionalInt min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    public /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.onClose(runnable));
    }

    public /* synthetic */ IntStream peek(IntConsumer intConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer)));
    }

    public /* synthetic */ int reduce(int i, IntBinaryOperator intBinaryOperator) {
        return this.wrappedValue.reduce(i, C$r8$wrapper$java$util$function$IntBinaryOperator$WRP.convert(intBinaryOperator));
    }

    public /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$IntBinaryOperator$WRP.convert(intBinaryOperator)));
    }

    public /* synthetic */ IntStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    public /* synthetic */ IntStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    public /* synthetic */ int sum() {
        return this.wrappedValue.sum();
    }

    public /* synthetic */ IntSummaryStatistics summaryStatistics() {
        return IntSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    public /* synthetic */ int[] toArray() {
        return this.wrappedValue.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.unordered());
    }
}
