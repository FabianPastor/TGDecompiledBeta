package j$.wrappers;

import j$.util.function.LongToIntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongToIntFunction$-V-WRP  reason: invalid class name */
/* compiled from: LongToIntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongToIntFunction$VWRP implements LongToIntFunction {
    final /* synthetic */ java.util.function.LongToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongToIntFunction$VWRP(java.util.function.LongToIntFunction longToIntFunction) {
        this.wrappedValue = longToIntFunction;
    }

    public static /* synthetic */ LongToIntFunction convert(java.util.function.LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof C$r8$wrapper$java$util$function$LongToIntFunction$WRP ? ((C$r8$wrapper$java$util$function$LongToIntFunction$WRP) longToIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongToIntFunction$VWRP(longToIntFunction);
    }

    public /* synthetic */ int applyAsInt(long j) {
        return this.wrappedValue.applyAsInt(j);
    }
}
