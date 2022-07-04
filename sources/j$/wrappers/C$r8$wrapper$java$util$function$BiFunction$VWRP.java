package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.Function;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiFunction$-V-WRP  reason: invalid class name */
/* compiled from: BiFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiFunction$VWRP implements BiFunction {
    final /* synthetic */ java.util.function.BiFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiFunction$VWRP(java.util.function.BiFunction biFunction) {
        this.wrappedValue = biFunction;
    }

    public static /* synthetic */ BiFunction convert(java.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof C$r8$wrapper$java$util$function$BiFunction$WRP ? ((C$r8$wrapper$java$util$function$BiFunction$WRP) biFunction).wrappedValue : new C$r8$wrapper$java$util$function$BiFunction$VWRP(biFunction);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.wrappedValue.apply(obj, obj2);
    }
}
