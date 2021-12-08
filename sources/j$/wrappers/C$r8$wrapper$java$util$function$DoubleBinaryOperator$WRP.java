package j$.wrappers;

import java.util.function.DoubleBinaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleBinaryOperator$-WRP  reason: invalid class name */
/* compiled from: DoubleBinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP implements DoubleBinaryOperator {
    final /* synthetic */ j$.util.function.DoubleBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP(j$.util.function.DoubleBinaryOperator doubleBinaryOperator) {
        this.wrappedValue = doubleBinaryOperator;
    }

    public static /* synthetic */ DoubleBinaryOperator convert(j$.util.function.DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP) doubleBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP(doubleBinaryOperator);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.wrappedValue.applyAsDouble(d, d2);
    }
}
