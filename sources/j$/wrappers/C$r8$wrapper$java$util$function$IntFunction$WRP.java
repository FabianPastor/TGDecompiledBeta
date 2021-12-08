package j$.wrappers;

import java.util.function.IntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntFunction$-WRP  reason: invalid class name */
/* compiled from: IntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntFunction$WRP implements IntFunction {
    final /* synthetic */ j$.util.function.IntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntFunction$WRP(j$.util.function.IntFunction intFunction) {
        this.wrappedValue = intFunction;
    }

    public static /* synthetic */ IntFunction convert(j$.util.function.IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof C$r8$wrapper$java$util$function$IntFunction$VWRP ? ((C$r8$wrapper$java$util$function$IntFunction$VWRP) intFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntFunction$WRP(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.wrappedValue.apply(i);
    }
}
