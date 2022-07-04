package j$.wrappers;

import j$.util.function.DoubleBinaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleBinaryOperator$-V-WRP  reason: invalid class name */
/* compiled from: DoubleBinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP implements DoubleBinaryOperator {
    final /* synthetic */ java.util.function.DoubleBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP(java.util.function.DoubleBinaryOperator doubleBinaryOperator) {
        this.wrappedValue = doubleBinaryOperator;
    }

    public static /* synthetic */ DoubleBinaryOperator convert(java.util.function.DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP ? ((C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP) doubleBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP(doubleBinaryOperator);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.wrappedValue.applyAsDouble(d, d2);
    }
}
