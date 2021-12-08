package j$.wrappers;

import j$.util.PrimitiveIterator;
import j$.util.function.Consumer;
import j$.util.function.LongConsumer;
import java.util.PrimitiveIterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfLong$-V-WRP  reason: invalid class name */
/* compiled from: PrimitiveIterator */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfLong$VWRP implements PrimitiveIterator.OfLong {
    final /* synthetic */ PrimitiveIterator.OfLong wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfLong$VWRP(PrimitiveIterator.OfLong ofLong) {
        this.wrappedValue = ofLong;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong convert(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfLong$WRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfLong$WRP) ofLong).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfLong$VWRP(ofLong);
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

    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.wrappedValue.nextLong();
    }

    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
