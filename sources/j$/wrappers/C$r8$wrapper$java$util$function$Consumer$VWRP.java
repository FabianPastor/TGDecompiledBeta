package j$.wrappers;

import j$.util.function.Consumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Consumer$-V-WRP  reason: invalid class name */
/* compiled from: Consumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Consumer$VWRP implements Consumer {
    final /* synthetic */ java.util.function.Consumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Consumer$VWRP(java.util.function.Consumer consumer) {
        this.wrappedValue = consumer;
    }

    public static /* synthetic */ Consumer convert(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof C$r8$wrapper$java$util$function$Consumer$WRP ? ((C$r8$wrapper$java$util$function$Consumer$WRP) consumer).wrappedValue : new C$r8$wrapper$java$util$function$Consumer$VWRP(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.wrappedValue.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer)));
    }
}
