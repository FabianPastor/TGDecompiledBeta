package j$.wrappers;

import j$.util.function.IntToDoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntToDoubleFunction$-V-WRP  reason: invalid class name */
/* compiled from: IntToDoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntToDoubleFunction$VWRP implements IntToDoubleFunction {
    final /* synthetic */ java.util.function.IntToDoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntToDoubleFunction$VWRP(java.util.function.IntToDoubleFunction intToDoubleFunction) {
        this.wrappedValue = intToDoubleFunction;
    }

    public static /* synthetic */ IntToDoubleFunction convert(java.util.function.IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return intToDoubleFunction instanceof C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP ? ((C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP) intToDoubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntToDoubleFunction$VWRP(intToDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(int i) {
        return this.wrappedValue.applyAsDouble(i);
    }
}
