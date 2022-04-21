package j$.wrappers;

import j$.util.function.LongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongFunction$-V-WRP  reason: invalid class name */
/* compiled from: LongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongFunction$VWRP implements LongFunction {
    final /* synthetic */ java.util.function.LongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongFunction$VWRP(java.util.function.LongFunction longFunction) {
        this.wrappedValue = longFunction;
    }

    public static /* synthetic */ LongFunction convert(java.util.function.LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof C$r8$wrapper$java$util$function$LongFunction$WRP ? ((C$r8$wrapper$java$util$function$LongFunction$WRP) longFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongFunction$VWRP(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.wrappedValue.apply(j);
    }
}
