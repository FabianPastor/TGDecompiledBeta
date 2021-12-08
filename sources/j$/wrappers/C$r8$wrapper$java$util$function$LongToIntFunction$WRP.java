package j$.wrappers;

import java.util.function.LongToIntFunction;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongToIntFunction$-WRP  reason: invalid class name */
/* compiled from: LongToIntFunction */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongToIntFunction$WRP implements LongToIntFunction {
    final /* synthetic */ j$.util.function.LongToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongToIntFunction$WRP(j$.util.function.LongToIntFunction longToIntFunction) {
        this.wrappedValue = longToIntFunction;
    }

    public static /* synthetic */ LongToIntFunction convert(j$.util.function.LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof C$r8$wrapper$java$util$function$LongToIntFunction$VWRP ? ((C$r8$wrapper$java$util$function$LongToIntFunction$VWRP) longToIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongToIntFunction$WRP(longToIntFunction);
    }

    public /* synthetic */ int applyAsInt(long j) {
        return this.wrappedValue.applyAsInt(j);
    }
}
