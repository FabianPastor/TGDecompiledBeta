package j$.wrappers;

import j$.util.function.IntBinaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntBinaryOperator$-V-WRP  reason: invalid class name */
/* compiled from: IntBinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntBinaryOperator$VWRP implements IntBinaryOperator {
    final /* synthetic */ java.util.function.IntBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntBinaryOperator$VWRP(java.util.function.IntBinaryOperator intBinaryOperator) {
        this.wrappedValue = intBinaryOperator;
    }

    public static /* synthetic */ IntBinaryOperator convert(java.util.function.IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof C$r8$wrapper$java$util$function$IntBinaryOperator$WRP ? ((C$r8$wrapper$java$util$function$IntBinaryOperator$WRP) intBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$IntBinaryOperator$VWRP(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.wrappedValue.applyAsInt(i, i2);
    }
}
