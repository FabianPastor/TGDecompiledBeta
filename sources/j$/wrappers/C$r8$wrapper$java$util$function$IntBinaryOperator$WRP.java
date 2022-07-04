package j$.wrappers;

import java.util.function.IntBinaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntBinaryOperator$-WRP  reason: invalid class name */
/* compiled from: IntBinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntBinaryOperator$WRP implements IntBinaryOperator {
    final /* synthetic */ j$.util.function.IntBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntBinaryOperator$WRP(j$.util.function.IntBinaryOperator intBinaryOperator) {
        this.wrappedValue = intBinaryOperator;
    }

    public static /* synthetic */ IntBinaryOperator convert(j$.util.function.IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof C$r8$wrapper$java$util$function$IntBinaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$IntBinaryOperator$VWRP) intBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$IntBinaryOperator$WRP(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.wrappedValue.applyAsInt(i, i2);
    }
}
