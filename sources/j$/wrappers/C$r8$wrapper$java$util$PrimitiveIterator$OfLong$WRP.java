package j$.wrappers;

import j$.util.PrimitiveIterator;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfLong$-WRP  reason: invalid class name */
/* compiled from: PrimitiveIterator */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfLong$WRP implements PrimitiveIterator.OfLong {
    final /* synthetic */ PrimitiveIterator.OfLong wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfLong$WRP(PrimitiveIterator.OfLong ofLong) {
        this.wrappedValue = ofLong;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong convert(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfLong$VWRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfLong$VWRP) ofLong).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfLong$WRP(ofLong);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.wrappedValue.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining((j$.util.function.Consumer<? super Long>) C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer));
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfLong] */
    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.wrappedValue.nextLong();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.PrimitiveIterator$OfLong] */
    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
