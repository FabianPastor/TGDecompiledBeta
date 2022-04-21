package j$.wrappers;

import j$.util.PrimitiveIterator;
import j$.util.function.Consumer;
import j$.util.function.IntConsumer;
import java.util.PrimitiveIterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfInt$-V-WRP  reason: invalid class name */
/* compiled from: PrimitiveIterator */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP implements PrimitiveIterator.OfInt {
    final /* synthetic */ PrimitiveIterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP(PrimitiveIterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt convert(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP(ofInt);
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

    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.wrappedValue.nextInt();
    }

    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
