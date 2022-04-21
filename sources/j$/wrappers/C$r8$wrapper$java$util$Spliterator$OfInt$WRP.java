package j$.wrappers;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfInt$-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfInt$WRP implements Spliterator.OfInt {
    final /* synthetic */ Spliterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfInt$WRP(Spliterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ Spliterator.OfInt convert(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$Spliterator$OfInt$VWRP ? ((C$r8$wrapper$java$util$Spliterator$OfInt$VWRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfInt$WRP(ofInt);
    }

    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.wrappedValue.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining((j$.util.function.Consumer<? super Integer>) C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$IntConsumer$VWRP.convert(intConsumer));
    }

    public /* synthetic */ Comparator getComparator() {
        return this.wrappedValue.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return this.wrappedValue.getExactSizeIfKnown();
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.wrappedValue.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.wrappedValue.tryAdvance(obj);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance((j$.util.function.Consumer<? super Integer>) C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$IntConsumer$VWRP.convert(intConsumer));
    }
}
