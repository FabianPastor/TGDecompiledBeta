package j$.wrappers;

import j$.util.DoubleSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$DoubleStream$-WRP  reason: invalid class name */
/* compiled from: DoubleStream */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$DoubleStream$WRP implements DoubleStream {
    final /* synthetic */ j$.util.stream.DoubleStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$DoubleStream$WRP(j$.util.stream.DoubleStream doubleStream) {
        this.wrappedValue = doubleStream;
    }

    public static /* synthetic */ DoubleStream convert(j$.util.stream.DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof C$r8$wrapper$java$util$stream$DoubleStream$VWRP ? ((C$r8$wrapper$java$util$stream$DoubleStream$VWRP) doubleStream).wrappedValue : new C$r8$wrapper$java$util$stream$DoubleStream$WRP(doubleStream);
    }

    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    public /* synthetic */ OptionalDouble average() {
        return OptionalConversions.convert(this.wrappedValue.average());
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.boxed());
    }

    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$VWRP.convert(supplier), C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP.convert(objDoubleConsumer), C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
    }

    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    public /* synthetic */ DoubleStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate)));
    }

    public /* synthetic */ OptionalDouble findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    public /* synthetic */ OptionalDouble findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$DoubleFunction$VWRP.convert(doubleFunction)));
    }

    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    public /* synthetic */ DoubleStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP.convert(doubleUnaryOperator)));
    }

    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP.convert(doubleToIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$WRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP.convert(doubleToLongFunction)));
    }

    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$DoubleFunction$VWRP.convert(doubleFunction)));
    }

    public /* synthetic */ OptionalDouble max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    public /* synthetic */ OptionalDouble min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.onClose(runnable));
    }

    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer)));
    }

    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.wrappedValue.reduce(d, C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP.convert(doubleBinaryOperator));
    }

    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP.convert(doubleBinaryOperator)));
    }

    public /* synthetic */ DoubleStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    public /* synthetic */ DoubleStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    public /* synthetic */ double sum() {
        return this.wrappedValue.sum();
    }

    public /* synthetic */ DoubleSummaryStatistics summaryStatistics() {
        return DoubleSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    public /* synthetic */ double[] toArray() {
        return this.wrappedValue.toArray();
    }

    public /* synthetic */ BaseStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.unordered());
    }
}
