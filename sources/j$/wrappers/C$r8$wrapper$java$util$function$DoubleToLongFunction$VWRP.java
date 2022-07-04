package j$.wrappers;

import j$.util.function.DoubleToLongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleToLongFunction$-V-WRP  reason: invalid class name */
/* compiled from: DoubleToLongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP implements DoubleToLongFunction {
    final /* synthetic */ java.util.function.DoubleToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP(java.util.function.DoubleToLongFunction doubleToLongFunction) {
        this.wrappedValue = doubleToLongFunction;
    }

    public static /* synthetic */ DoubleToLongFunction convert(java.util.function.DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP ? ((C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP) doubleToLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.wrappedValue.applyAsLong(d);
    }
}
