package j$.wrappers;

import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjDoubleConsumer$-WRP  reason: invalid class name */
/* compiled from: ObjDoubleConsumer */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP implements ObjDoubleConsumer {
    final /* synthetic */ j$.util.function.ObjDoubleConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP(j$.util.function.ObjDoubleConsumer objDoubleConsumer) {
        this.wrappedValue = objDoubleConsumer;
    }

    public static /* synthetic */ ObjDoubleConsumer convert(j$.util.function.ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP ? ((C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP) objDoubleConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.wrappedValue.accept(obj, d);
    }
}
