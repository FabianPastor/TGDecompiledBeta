package j$.wrappers;

import j$.util.PrimitiveIterator;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfDouble$-WRP  reason: invalid class name */
/* compiled from: PrimitiveIterator */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$WRP implements PrimitiveIterator.OfDouble {
    final /* synthetic */ PrimitiveIterator.OfDouble wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$WRP(PrimitiveIterator.OfDouble ofDouble) {
        this.wrappedValue = ofDouble;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble convert(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$VWRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$VWRP) ofDouble).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$WRP(ofDouble);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.wrappedValue.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining((j$.util.function.Consumer<? super Double>) C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfDouble] */
    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    public /* synthetic */ double nextDouble() {
        return this.wrappedValue.nextDouble();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfDouble] */
    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
