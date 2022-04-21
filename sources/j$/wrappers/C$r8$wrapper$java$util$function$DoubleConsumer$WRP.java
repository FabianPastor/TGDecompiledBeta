package j$.wrappers;

import java.util.function.DoubleConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleConsumer$-WRP  reason: invalid class name */
/* compiled from: DoubleConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleConsumer$WRP implements DoubleConsumer {
    final /* synthetic */ j$.util.function.DoubleConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleConsumer$WRP(j$.util.function.DoubleConsumer doubleConsumer) {
        this.wrappedValue = doubleConsumer;
    }

    public static /* synthetic */ DoubleConsumer convert(j$.util.function.DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof C$r8$wrapper$java$util$function$DoubleConsumer$VWRP ? ((C$r8$wrapper$java$util$function$DoubleConsumer$VWRP) doubleConsumer).wrappedValue : new C$r8$wrapper$java$util$function$DoubleConsumer$WRP(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.wrappedValue.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer)));
    }
}
