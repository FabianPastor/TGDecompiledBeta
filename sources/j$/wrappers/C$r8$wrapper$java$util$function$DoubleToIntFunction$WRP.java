package j$.wrappers;

import java.util.function.DoubleToIntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleToIntFunction$-WRP  reason: invalid class name */
/* compiled from: DoubleToIntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP implements DoubleToIntFunction {
    final /* synthetic */ j$.util.function.DoubleToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP(j$.util.function.DoubleToIntFunction doubleToIntFunction) {
        this.wrappedValue = doubleToIntFunction;
    }

    public static /* synthetic */ DoubleToIntFunction convert(j$.util.function.DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return doubleToIntFunction instanceof C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP ? ((C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP) doubleToIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP(doubleToIntFunction);
    }

    public /* synthetic */ int applyAsInt(double d) {
        return this.wrappedValue.applyAsInt(d);
    }
}
