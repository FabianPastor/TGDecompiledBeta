package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.LongConsumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfLong$-V-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfLong$VWRP implements Spliterator.OfLong {
    final /* synthetic */ Spliterator.OfLong wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfLong$VWRP(Spliterator.OfLong ofLong) {
        this.wrappedValue = ofLong;
    }

    public static /* synthetic */ Spliterator.OfLong convert(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C$r8$wrapper$java$util$Spliterator$OfLong$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfLong$WRP) ofLong).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfLong$VWRP(ofLong);
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

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
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

    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.wrappedValue.tryAdvance(obj);
    }
}
