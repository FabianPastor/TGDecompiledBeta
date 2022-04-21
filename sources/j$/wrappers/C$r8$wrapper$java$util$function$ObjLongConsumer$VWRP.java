package j$.wrappers;

import j$.util.function.ObjLongConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjLongConsumer$-V-WRP  reason: invalid class name */
/* compiled from: ObjLongConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP implements ObjLongConsumer {
    final /* synthetic */ java.util.function.ObjLongConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP(java.util.function.ObjLongConsumer objLongConsumer) {
        this.wrappedValue = objLongConsumer;
    }

    public static /* synthetic */ ObjLongConsumer convert(java.util.function.ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof C$r8$wrapper$java$util$function$ObjLongConsumer$WRP ? ((C$r8$wrapper$java$util$function$ObjLongConsumer$WRP) objLongConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP(objLongConsumer);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.wrappedValue.accept(obj, j);
    }
}
