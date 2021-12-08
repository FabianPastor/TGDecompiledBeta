package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfPrimitive$-V-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfPrimitive$VWRP implements Spliterator.OfPrimitive {
    final /* synthetic */ Spliterator.OfPrimitive wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfPrimitive$VWRP(Spliterator.OfPrimitive ofPrimitive) {
        this.wrappedValue = ofPrimitive;
    }

    public static /* synthetic */ Spliterator.OfPrimitive convert(Spliterator.OfPrimitive ofPrimitive) {
        if (ofPrimitive == null) {
            return null;
        }
        return ofPrimitive instanceof C$r8$wrapper$java$util$Spliterator$OfPrimitive$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfPrimitive$WRP) ofPrimitive).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfPrimitive$VWRP(ofPrimitive);
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

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.wrappedValue.tryAdvance(obj);
    }
}
