package j$.wrappers;

import java.util.function.IntToDoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntToDoubleFunction$-WRP  reason: invalid class name */
/* compiled from: IntToDoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP implements IntToDoubleFunction {
    final /* synthetic */ j$.util.function.IntToDoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP(j$.util.function.IntToDoubleFunction intToDoubleFunction) {
        this.wrappedValue = intToDoubleFunction;
    }

    public static /* synthetic */ IntToDoubleFunction convert(j$.util.function.IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return intToDoubleFunction instanceof C$r8$wrapper$java$util$function$IntToDoubleFunction$VWRP ? ((C$r8$wrapper$java$util$function$IntToDoubleFunction$VWRP) intToDoubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntToDoubleFunction$WRP(intToDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(int i) {
        return this.wrappedValue.applyAsDouble(i);
    }
}
