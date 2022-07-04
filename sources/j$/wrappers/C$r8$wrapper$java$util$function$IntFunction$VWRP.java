package j$.wrappers;

import j$.util.function.IntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntFunction$-V-WRP  reason: invalid class name */
/* compiled from: IntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntFunction$VWRP implements IntFunction {
    final /* synthetic */ java.util.function.IntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntFunction$VWRP(java.util.function.IntFunction intFunction) {
        this.wrappedValue = intFunction;
    }

    public static /* synthetic */ IntFunction convert(java.util.function.IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof C$r8$wrapper$java$util$function$IntFunction$WRP ? ((C$r8$wrapper$java$util$function$IntFunction$WRP) intFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntFunction$VWRP(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.wrappedValue.apply(i);
    }
}
