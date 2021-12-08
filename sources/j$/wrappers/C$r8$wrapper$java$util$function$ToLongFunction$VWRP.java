package j$.wrappers;

import j$.util.function.ToLongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ToLongFunction$-V-WRP  reason: invalid class name */
/* compiled from: ToLongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ToLongFunction$VWRP implements ToLongFunction {
    final /* synthetic */ java.util.function.ToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ToLongFunction$VWRP(java.util.function.ToLongFunction toLongFunction) {
        this.wrappedValue = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction convert(java.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof C$r8$wrapper$java$util$function$ToLongFunction$WRP ? ((C$r8$wrapper$java$util$function$ToLongFunction$WRP) toLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$ToLongFunction$VWRP(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.wrappedValue.applyAsLong(obj);
    }
}
