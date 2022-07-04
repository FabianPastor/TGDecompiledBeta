package j$.wrappers;

import j$.util.OptionalConversions;
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

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$Stream$-WRP  reason: invalid class name */
/* compiled from: Stream */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Stream$WRP implements Stream {
    final /* synthetic */ j$.util.stream.Stream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Stream$WRP(j$.util.stream.Stream stream) {
        this.wrappedValue = stream;
    }

    public static /* synthetic */ Stream convert(j$.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof C$r8$wrapper$java$util$stream$Stream$VWRP ? ((C$r8$wrapper$java$util$stream$Stream$VWRP) stream).wrappedValue : new C$r8$wrapper$java$util$stream$Stream$WRP(stream);
    }

    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
    }

    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
    }

    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$VWRP.convert(supplier), C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer), C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer2));
    }

    public /* synthetic */ Object collect(Collector collector) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$stream$Collector$VWRP.convert(collector));
    }

    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    public /* synthetic */ Stream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    public /* synthetic */ Stream filter(Predicate predicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate)));
    }

    public /* synthetic */ Optional findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    public /* synthetic */ Stream flatMap(Function function) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return C$r8$wrapper$java$util$stream$DoubleStream$WRP.convert(this.wrappedValue.flatMapToDouble(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.wrappedValue.flatMapToInt(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return C$r8$wrapper$java$util$stream$LongStream$WRP.convert(this.wrappedValue.flatMapToLong(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.wrappedValue.iterator();
    }

    public /* synthetic */ Stream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    public /* synthetic */ Stream map(Function function) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return C$r8$wrapper$java$util$stream$DoubleStream$WRP.convert(this.wrappedValue.mapToDouble(C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP.convert(toDoubleFunction)));
    }

    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$ToIntFunction$VWRP.convert(toIntFunction)));
    }

    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$WRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$ToLongFunction$VWRP.convert(toLongFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return OptionalConversions.convert(this.wrappedValue.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return OptionalConversions.convert(this.wrappedValue.min(comparator));
    }

    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.parallel());
    }

    public /* synthetic */ Stream peek(Consumer consumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer)));
    }

    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.wrappedValue.reduce(obj, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction), C$r8$wrapper$java$util$function$BinaryOperator$VWRP.convert(binaryOperator));
    }

    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.wrappedValue.reduce(obj, C$r8$wrapper$java$util$function$BinaryOperator$VWRP.convert(binaryOperator));
    }

    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$BinaryOperator$VWRP.convert(binaryOperator)));
    }

    public /* synthetic */ BaseStream sequential() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return convert(this.wrappedValue.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return C$r8$wrapper$java$util$Spliterator$WRP.convert(this.wrappedValue.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.wrappedValue.toArray();
    }

    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.wrappedValue.toArray(C$r8$wrapper$java$util$function$IntFunction$VWRP.convert(intFunction));
    }

    public /* synthetic */ BaseStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.unordered());
    }
}
