package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$-V-WRP  reason: invalid class name */
/* compiled from: Spliterator */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$VWRP implements Spliterator {
    final /* synthetic */ java.util.Spliterator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$VWRP(java.util.Spliterator spliterator) {
        this.wrappedValue = spliterator;
    }

    public static /* synthetic */ Spliterator convert(java.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof C$r8$wrapper$java$util$Spliterator$WRP ? ((C$r8$wrapper$java$util$Spliterator$WRP) spliterator).wrappedValue : new C$r8$wrapper$java$util$Spliterator$VWRP(spliterator);
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

    public /* synthetic */ Spliterator trySplit() {
        return convert(this.wrappedValue.trySplit());
    }
}
