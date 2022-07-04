package j$.wrappers;

import java.util.function.ToIntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ToIntFunction$-WRP  reason: invalid class name */
/* compiled from: ToIntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ToIntFunction$WRP implements ToIntFunction {
    final /* synthetic */ j$.util.function.ToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ToIntFunction$WRP(j$.util.function.ToIntFunction toIntFunction) {
        this.wrappedValue = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction convert(j$.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof C$r8$wrapper$java$util$function$ToIntFunction$VWRP ? ((C$r8$wrapper$java$util$function$ToIntFunction$VWRP) toIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$ToIntFunction$WRP(toIntFunction);
    }

    public /* synthetic */ int applyAsInt(Object obj) {
        return this.wrappedValue.applyAsInt(obj);
    }
}
