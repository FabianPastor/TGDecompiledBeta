package j$.wrappers;

import j$.util.LongSummaryStatistics;
import j$.util.LongSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import j$.util.OptionalDouble;
import j$.util.OptionalLong;
import j$.util.function.BiConsumer;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.function.LongPredicate;
import j$.util.function.LongToDoubleFunction;
import j$.util.function.LongToIntFunction;
import j$.util.function.LongUnaryOperator;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.BaseStream;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$LongStream$-V-WRP  reason: invalid class name */
/* compiled from: LongStream */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$LongStream$VWRP implements LongStream {
    final /* synthetic */ java.util.stream.LongStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$LongStream$VWRP(java.util.stream.LongStream longStream) {
        this.wrappedValue = longStream;
    }

    public static /* synthetic */ LongStream convert(java.util.stream.LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof C$r8$wrapper$java$util$stream$LongStream$WRP ? ((C$r8$wrapper$java$util$stream$LongStream$WRP) longStream).wrappedValue : new C$r8$wrapper$java$util$stream$LongStream$VWRP(longStream);
    }

    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate));
    }

    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate));
    }

    public /* synthetic */ DoubleStream asDoubleStream() {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.asDoubleStream());
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

    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$WRP.convert(supplier), C$r8$wrapper$java$util$function$ObjLongConsumer$WRP.convert(objLongConsumer), C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    public /* synthetic */ LongStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate)));
    }

    public /* synthetic */ OptionalLong findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    public /* synthetic */ OptionalLong findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$LongFunction$WRP.convert(longFunction)));
    }

    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
    }

    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    public /* synthetic */ LongStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$LongUnaryOperator$WRP.convert(longUnaryOperator)));
    }

    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.mapToDouble(C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP.convert(longToDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$LongToIntFunction$WRP.convert(longToIntFunction)));
    }

    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$LongFunction$WRP.convert(longFunction)));
    }

    public /* synthetic */ OptionalLong max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    public /* synthetic */ OptionalLong min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.onClose(runnable));
    }

    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer)));
    }

    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.wrappedValue.reduce(j, C$r8$wrapper$java$util$function$LongBinaryOperator$WRP.convert(longBinaryOperator));
    }

    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$LongBinaryOperator$WRP.convert(longBinaryOperator)));
    }

    public /* synthetic */ LongStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    public /* synthetic */ LongStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    public /* synthetic */ long sum() {
        return this.wrappedValue.sum();
    }

    public /* synthetic */ LongSummaryStatistics summaryStatistics() {
        return LongSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    public /* synthetic */ long[] toArray() {
        return this.wrappedValue.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.unordered());
    }
}
