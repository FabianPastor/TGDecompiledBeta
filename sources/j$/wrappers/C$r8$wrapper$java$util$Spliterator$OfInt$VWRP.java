package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.IntConsumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfInt$-V-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfInt$VWRP implements Spliterator.OfInt {
    final /* synthetic */ Spliterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfInt$VWRP(Spliterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ Spliterator.OfInt convert(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$Spliterator$OfInt$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfInt$WRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfInt$VWRP(ofInt);
    }

    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.wrappedValue.forEachRemaining(obj);
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

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.wrappedValue.tryAdvance(obj);
    }
}
