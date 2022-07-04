package j$.wrappers;

import java.util.function.DoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleFunction$-WRP  reason: invalid class name */
/* compiled from: DoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleFunction$WRP implements DoubleFunction {
    final /* synthetic */ j$.util.function.DoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleFunction$WRP(j$.util.function.DoubleFunction doubleFunction) {
        this.wrappedValue = doubleFunction;
    }

    public static /* synthetic */ DoubleFunction convert(j$.util.function.DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof C$r8$wrapper$java$util$function$DoubleFunction$VWRP ? ((C$r8$wrapper$java$util$function$DoubleFunction$VWRP) doubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleFunction$WRP(doubleFunction);
    }

    public /* synthetic */ Object apply(double d) {
        return this.wrappedValue.apply(d);
    }
}
