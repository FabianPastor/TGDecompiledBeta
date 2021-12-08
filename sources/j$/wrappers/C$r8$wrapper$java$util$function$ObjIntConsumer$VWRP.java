package j$.wrappers;

import j$.util.function.ObjIntConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjIntConsumer$-V-WRP  reason: invalid class name */
/* compiled from: ObjIntConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjIntConsumer$VWRP implements ObjIntConsumer {
    final /* synthetic */ java.util.function.ObjIntConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjIntConsumer$VWRP(java.util.function.ObjIntConsumer objIntConsumer) {
        this.wrappedValue = objIntConsumer;
    }

    public static /* synthetic */ ObjIntConsumer convert(java.util.function.ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof C$r8$wrapper$java$util$function$ObjIntConsumer$WRP ? ((C$r8$wrapper$java$util$function$ObjIntConsumer$WRP) objIntConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjIntConsumer$VWRP(objIntConsumer);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.wrappedValue.accept(obj, i);
    }
}
