package j$.wrappers;

import java.util.function.LongFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongFunction$-WRP  reason: invalid class name */
/* compiled from: LongFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongFunction$WRP implements LongFunction {
    final /* synthetic */ j$.util.function.LongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongFunction$WRP(j$.util.function.LongFunction longFunction) {
        this.wrappedValue = longFunction;
    }

    public static /* synthetic */ LongFunction convert(j$.util.function.LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof C$r8$wrapper$java$util$function$LongFunction$VWRP ? ((C$r8$wrapper$java$util$function$LongFunction$VWRP) longFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongFunction$WRP(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.wrappedValue.apply(j);
    }
}
