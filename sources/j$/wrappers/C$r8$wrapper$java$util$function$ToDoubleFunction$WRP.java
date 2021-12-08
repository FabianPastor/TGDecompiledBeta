package j$.wrappers;

import java.util.function.ToDoubleFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ToDoubleFunction$-WRP  reason: invalid class name */
/* compiled from: ToDoubleFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ToDoubleFunction$WRP implements ToDoubleFunction {
    final /* synthetic */ j$.util.function.ToDoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ToDoubleFunction$WRP(j$.util.function.ToDoubleFunction toDoubleFunction) {
        this.wrappedValue = toDoubleFunction;
    }

    public static /* synthetic */ ToDoubleFunction convert(j$.util.function.ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP ? ((C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP) toDoubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$ToDoubleFunction$WRP(toDoubleFunction);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.wrappedValue.applyAsDouble(obj);
    }
}
