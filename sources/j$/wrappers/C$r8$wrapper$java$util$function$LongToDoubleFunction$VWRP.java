package j$.wrappers;

import j$.util.function.LongToDoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongToDoubleFunction$-V-WRP  reason: invalid class name */
/* compiled from: LongToDoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP implements LongToDoubleFunction {
    final /* synthetic */ java.util.function.LongToDoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP(java.util.function.LongToDoubleFunction longToDoubleFunction) {
        this.wrappedValue = longToDoubleFunction;
    }

    public static /* synthetic */ LongToDoubleFunction convert(java.util.function.LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP ? ((C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP) longToDoubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP(longToDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(long j) {
        return this.wrappedValue.applyAsDouble(j);
    }
}
