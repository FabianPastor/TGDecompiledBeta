package j$.wrappers;

import j$.util.PrimitiveIterator;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfInt$-WRP  reason: invalid class name */
/* compiled from: PrimitiveIterator */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP implements PrimitiveIterator.OfInt {
    final /* synthetic */ PrimitiveIterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP(PrimitiveIterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt convert(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP(ofInt);
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

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfInt] */
    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.wrappedValue.nextInt();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfInt] */
    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
