package j$.wrappers;

import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongConsumer$-WRP  reason: invalid class name */
/* compiled from: LongConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongConsumer$WRP implements LongConsumer {
    final /* synthetic */ j$.util.function.LongConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongConsumer$WRP(j$.util.function.LongConsumer longConsumer) {
        this.wrappedValue = longConsumer;
    }

    public static /* synthetic */ LongConsumer convert(j$.util.function.LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof C$r8$wrapper$java$util$function$LongConsumer$VWRP ? ((C$r8$wrapper$java$util$function$LongConsumer$VWRP) longConsumer).wrappedValue : new C$r8$wrapper$java$util$function$LongConsumer$WRP(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.wrappedValue.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer)));
    }
}
