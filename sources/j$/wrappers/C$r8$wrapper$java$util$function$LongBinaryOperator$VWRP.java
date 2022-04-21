package j$.wrappers;

import j$.util.function.LongBinaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongBinaryOperator$-V-WRP  reason: invalid class name */
/* compiled from: LongBinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP implements LongBinaryOperator {
    final /* synthetic */ java.util.function.LongBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP(java.util.function.LongBinaryOperator longBinaryOperator) {
        this.wrappedValue = longBinaryOperator;
    }

    public static /* synthetic */ LongBinaryOperator convert(java.util.function.LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof C$r8$wrapper$java$util$function$LongBinaryOperator$WRP ? ((C$r8$wrapper$java$util$function$LongBinaryOperator$WRP) longBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP(longBinaryOperator);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.wrappedValue.applyAsLong(j, j2);
    }
}
