package j$.wrappers;

import j$.util.function.IntConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntConsumer$-V-WRP  reason: invalid class name */
/* compiled from: IntConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntConsumer$VWRP implements IntConsumer {
    final /* synthetic */ java.util.function.IntConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntConsumer$VWRP(java.util.function.IntConsumer intConsumer) {
        this.wrappedValue = intConsumer;
    }

    public static /* synthetic */ IntConsumer convert(java.util.function.IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof C$r8$wrapper$java$util$function$IntConsumer$WRP ? ((C$r8$wrapper$java$util$function$IntConsumer$WRP) intConsumer).wrappedValue : new C$r8$wrapper$java$util$function$IntConsumer$VWRP(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.wrappedValue.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer)));
    }
}
