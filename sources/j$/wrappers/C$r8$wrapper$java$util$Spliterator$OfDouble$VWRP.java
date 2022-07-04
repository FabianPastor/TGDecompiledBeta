package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfDouble$-V-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfDouble$VWRP implements Spliterator.OfDouble {
    final /* synthetic */ Spliterator.OfDouble wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfDouble$VWRP(Spliterator.OfDouble ofDouble) {
        this.wrappedValue = ofDouble;
    }

    public static /* synthetic */ Spliterator.OfDouble convert(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof C$r8$wrapper$java$util$Spliterator$OfDouble$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfDouble$WRP) ofDouble).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfDouble$VWRP(ofDouble);
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

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer));
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

    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.wrappedValue.tryAdvance(obj);
    }
}
