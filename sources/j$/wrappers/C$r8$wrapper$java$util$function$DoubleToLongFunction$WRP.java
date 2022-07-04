package j$.wrappers;

import java.util.function.DoubleToLongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleToLongFunction$-WRP  reason: invalid class name */
/* compiled from: DoubleToLongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP implements DoubleToLongFunction {
    final /* synthetic */ j$.util.function.DoubleToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP(j$.util.function.DoubleToLongFunction doubleToLongFunction) {
        this.wrappedValue = doubleToLongFunction;
    }

    public static /* synthetic */ DoubleToLongFunction convert(j$.util.function.DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP ? ((C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP) doubleToLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP(doubleToLongFunction);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.wrappedValue.applyAsLong(d);
    }
}
