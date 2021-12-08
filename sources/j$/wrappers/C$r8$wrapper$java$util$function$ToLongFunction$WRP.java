package j$.wrappers;

import java.util.function.ToLongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ToLongFunction$-WRP  reason: invalid class name */
/* compiled from: ToLongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ToLongFunction$WRP implements ToLongFunction {
    final /* synthetic */ j$.util.function.ToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ToLongFunction$WRP(j$.util.function.ToLongFunction toLongFunction) {
        this.wrappedValue = toLongFunction;
    }

    public static /* synthetic */ ToLongFunction convert(j$.util.function.ToLongFunction toLongFunction) {
        if (toLongFunction == null) {
            return null;
        }
        return toLongFunction instanceof C$r8$wrapper$java$util$function$ToLongFunction$VWRP ? ((C$r8$wrapper$java$util$function$ToLongFunction$VWRP) toLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$ToLongFunction$WRP(toLongFunction);
    }

    public /* synthetic */ long applyAsLong(Object obj) {
        return this.wrappedValue.applyAsLong(obj);
    }
}
