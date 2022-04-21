package j$.wrappers;

import java.util.function.BiConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiConsumer$-WRP  reason: invalid class name */
/* compiled from: BiConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiConsumer$WRP implements BiConsumer {
    final /* synthetic */ j$.util.function.BiConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiConsumer$WRP(j$.util.function.BiConsumer biConsumer) {
        this.wrappedValue = biConsumer;
    }

    public static /* synthetic */ BiConsumer convert(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof C$r8$wrapper$java$util$function$BiConsumer$VWRP ? ((C$r8$wrapper$java$util$function$BiConsumer$VWRP) biConsumer).wrappedValue : new C$r8$wrapper$java$util$function$BiConsumer$WRP(biConsumer);
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.wrappedValue.accept(obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer)));
    }
}
