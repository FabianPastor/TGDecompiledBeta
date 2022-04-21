package j$.wrappers;

import j$.util.function.DoubleConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleConsumer$-V-WRP  reason: invalid class name */
/* compiled from: DoubleConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleConsumer$VWRP implements DoubleConsumer {
    final /* synthetic */ java.util.function.DoubleConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleConsumer$VWRP(java.util.function.DoubleConsumer doubleConsumer) {
        this.wrappedValue = doubleConsumer;
    }

    public static /* synthetic */ DoubleConsumer convert(java.util.function.DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof C$r8$wrapper$java$util$function$DoubleConsumer$WRP ? ((C$r8$wrapper$java$util$function$DoubleConsumer$WRP) doubleConsumer).wrappedValue : new C$r8$wrapper$java$util$function$DoubleConsumer$VWRP(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.wrappedValue.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer)));
    }
}
