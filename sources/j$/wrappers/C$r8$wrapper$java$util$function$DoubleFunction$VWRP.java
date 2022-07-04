package j$.wrappers;

import j$.util.function.DoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleFunction$-V-WRP  reason: invalid class name */
/* compiled from: DoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleFunction$VWRP implements DoubleFunction {
    final /* synthetic */ java.util.function.DoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleFunction$VWRP(java.util.function.DoubleFunction doubleFunction) {
        this.wrappedValue = doubleFunction;
    }

    public static /* synthetic */ DoubleFunction convert(java.util.function.DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof C$r8$wrapper$java$util$function$DoubleFunction$WRP ? ((C$r8$wrapper$java$util$function$DoubleFunction$WRP) doubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleFunction$VWRP(doubleFunction);
    }

    public /* synthetic */ Object apply(double d) {
        return this.wrappedValue.apply(d);
    }
}
