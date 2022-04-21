package j$.wrappers;

import j$.util.function.ObjDoubleConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjDoubleConsumer$-V-WRP  reason: invalid class name */
/* compiled from: ObjDoubleConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP implements ObjDoubleConsumer {
    final /* synthetic */ java.util.function.ObjDoubleConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP(java.util.function.ObjDoubleConsumer objDoubleConsumer) {
        this.wrappedValue = objDoubleConsumer;
    }

    public static /* synthetic */ ObjDoubleConsumer convert(java.util.function.ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP ? ((C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP) objDoubleConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.wrappedValue.accept(obj, d);
    }
}
